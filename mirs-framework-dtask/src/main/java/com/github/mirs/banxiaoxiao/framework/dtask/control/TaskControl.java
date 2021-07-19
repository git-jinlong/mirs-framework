package com.github.mirs.banxiaoxiao.framework.dtask.control;

import com.github.mirs.banxiaoxiao.framework.common.util.StringUtil;
import com.github.mirs.banxiaoxiao.framework.common.util.TaskExecutors;
import com.github.mirs.banxiaoxiao.framework.core.dcc.leader.LeaderSelector;
import com.github.mirs.banxiaoxiao.framework.core.dcc.leader.LeaderSelectorListener;
import com.github.mirs.banxiaoxiao.framework.core.dcc.lock.RandomDistributedReentrantLock;
import com.github.mirs.banxiaoxiao.framework.core.drpc.spring.DServiceBean;
import com.github.mirs.banxiaoxiao.framework.core.log.TComLogs;
import com.github.mirs.banxiaoxiao.framework.core.spring.SpringContextHolder;
import com.github.mirs.banxiaoxiao.framework.dtask.Constants;
import com.github.mirs.banxiaoxiao.framework.dtask.TaskException;
import com.github.mirs.banxiaoxiao.framework.dtask.conf.TaskConfig;
import com.github.mirs.banxiaoxiao.framework.dtask.conf.TaskConfigProvider;
import com.github.mirs.banxiaoxiao.framework.dtask.conf.mix.MixTaskConfigProvider;
import com.github.mirs.banxiaoxiao.framework.dtask.control.TaskExecutorProxyHolder.Proxy;
import com.github.mirs.banxiaoxiao.framework.dtask.control.proxy.TaskExecutorProxy;
import com.github.mirs.banxiaoxiao.framework.dtask.seres.ServerResource;
import com.github.mirs.banxiaoxiao.framework.dtask.seres.ServerResourceDiscovery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author zcy 2019年4月29日
 */
public class TaskControl<T extends TaskConfig> implements TaskStarter<T> {

    private TaskControlProperties taskControlProperties;

    private TaskConfigProvider<T> taskConfigProvider;

    private ServerResourceDiscovery serverResourceDiscovery;

    private AdjustAlgorithm<T> adjust;

    private BalanceAlgorithm<T> balancer;

    private DTaskControlLeader leader;

    private DTaskLock taskLock;

    private TaskExecutorProxyHolder<T> proxyHolder;

    private DServiceBean<TaskStarter<T>> serviceBean;

    public TaskControl(TaskControlProperties dtaskConfig) {
        super();
        this.taskControlProperties = dtaskConfig;
        this.proxyHolder = new TaskExecutorProxyHolder<T>(dtaskConfig);
    }

    public synchronized void init() {
        this.proxyHolder.init();
        this.inspectTime = 0;
        if (this.balancer instanceof TaskControlPropertiesAware) {
            ((TaskControlPropertiesAware) this.balancer).setTaskControlProperties(taskControlProperties);
        }
        if (this.adjust instanceof TaskControlPropertiesAware) {
            ((TaskControlPropertiesAware) this.adjust).setTaskControlProperties(taskControlProperties);
        }
        if (this.serverResourceDiscovery instanceof TaskControlPropertiesAware) {
            ((TaskControlPropertiesAware) this.serverResourceDiscovery).setTaskControlProperties(taskControlProperties);
        }
        if (this.taskConfigProvider instanceof TaskControlPropertiesAware) {
            ((TaskControlPropertiesAware) this.taskConfigProvider).setTaskControlProperties(taskControlProperties);
        }
        if (this.taskConfigProvider instanceof MixTaskConfigProvider) {
            ((MixTaskConfigProvider) this.taskConfigProvider).startSync();
        }

        this.serviceBean = new DServiceBean<>();
        this.serviceBean.setGroup(this.taskControlProperties.getTaskCode());
        this.serviceBean.setInterface(TaskStarter.class);
        this.serviceBean.setRef(this);
        this.serviceBean.getConfig().setRetries(0);
        this.serviceBean.setApplicationContext(SpringContextHolder.get());
        try {
            this.serviceBean.afterPropertiesSet();
        } catch (Exception e) {
            throw new TaskException("init TaskStarter service bean fail.", e);
        }
        this.serviceBean.export();
    }

    @Override
    public synchronized boolean startTask(T taskConfig) {
        TaskStatis statis = getTaskStatis();
        String taskId = taskConfig.getTaskId();
        try {
            lock(taskId);
            TaskExecutorProxy<T> executorProxy = this.proxyHolder.getTaskExecutorProxy(taskId);
            if (taskConfig.isEffective() && executorProxy == null) {
                if (!TaskLicenseChecker.check(taskControlProperties.getTaskCode(), statis.getTaskServerMap().size() + 1))
                    return false;// check license size
                ServerResource serverResource = balancer.balancer(taskConfig, statis, false);
                executorProxy = this.proxyHolder.createTaskExecutorProxy(taskConfig, serverResource);
                statis.putTaskServer(taskConfig.getTaskId(), serverResource.getId());
                executorProxy.start();
            }
            TComLogs.debug("start task [{} {}] config, effective = {}", getTaskControlProperties().getTaskCode(), taskConfig, taskConfig.isEffective());
        } catch (Throwable e) {
            TComLogs.error("start task [{} {}] config error", e, getTaskControlProperties().getTaskCode(), taskConfig);
            return false;
        } finally {
            unlock(taskId);
        }
        return true;
    }

    public synchronized void destory() {
        this.adjust = null;
        this.balancer = null;
        this.proxyHolder.clean();
        if (this.taskConfigProvider instanceof MixTaskConfigProvider) {
            ((MixTaskConfigProvider) this.taskConfigProvider).stopSync();
        }
        try {
            this.serviceBean.destroy();
        } catch (Exception e) {
            throw new TaskException("destroy TaskStarter service bean fail.", e);
        }
        this.serviceBean = null;
    }

    private int inspectTime = 0;

    public synchronized void inspect() {
        TComLogs.debug("[{}] auto inspect ...", getTaskControlProperties().getTaskCode());
        TaskStatis statis = getTaskStatis();
        int i = 0;
        while (true) {
            List<T> taskConfigs = taskConfigProvider.getTaskConfigs(i);
            i++;
            if (taskConfigs == null || taskConfigs.size() == 0) {
                break;
            }
            for (T taskConfig : taskConfigs) {
                inspectTaskConfig(taskConfig, statis);
            }
        }
        List<Proxy<T>> proxys = this.proxyHolder.getProxys();
        for (Proxy<T> executorProxy : proxys) {
            inspectTaskExecutor(executorProxy, statis);
        }
        if (inspectTime > 2) {
            inspectTime = 0;
            boolean allEndState = true;
            for (Proxy<T> proxy : proxys) {
                TaskExecutorProxy<T> executorProxy = proxy.taskProxy;
                if (!executorProxy.isRunning() && !executorProxy.isStoped()) {
                    allEndState = false;
                }
            }
            // 当所有的任务全部启动后再进行负载调整，不急于一时
            if (allEndState) {
                for (Proxy<T> proxy : proxys) {
                    TaskExecutorProxy<T> executorProxy = proxy.taskProxy;
                    String oldSr = proxy.serverResource.getId();
                    T config = proxy.taskConfig;
                    String taskId = proxy.taskId;
                    boolean needAdjust = this.adjust.needAdjust(config, statis);
                    TComLogs.debug("auto adjust task [{}] needAdjust={} oldServer={}", executorProxy, needAdjust, oldSr);
                    if (needAdjust) {
                        try {
                            ServerResource res = this.balancer.balancer(config, statis, true);
                            if (!res.getId().equals(oldSr)) {
                                executorProxy.stop();
                                statis.getTaskServerMap().remove(taskId);
                                executorProxy.setServerResource(res);
                                proxy.serverResource = res;
                                executorProxy.start();
                                statis.putTaskServer(taskId, res.getId());
                                TComLogs.info("move task [{}] success", executorProxy);
                            } else {
                                TComLogs.info("auto adjust task [{}] oldServer same newServer,no migration required", executorProxy, needAdjust,
                                        oldSr);
                            }
                        } catch (Throwable e) {
                            TComLogs.error("auto adjust task [{}] error occurred, needAdjust={} oldServer={}", e, executorProxy, needAdjust, oldSr);
                        }
                    }
                }
            }
        } else {
            inspectTime = inspectTime + 1;
        }
    }

    private TaskStatis getTaskStatis() {
        TaskStatis statis = new TaskStatis();
        Map<String, String> taskServerMap = new HashMap<String, String>();
        for (Proxy<T> proxy : this.proxyHolder.getProxys()) {
            taskServerMap.put(proxy.taskId, proxy.serverResource.getId());
        }
        statis.setTaskServerMap(taskServerMap);
        List<ServerResource> serverList = this.serverResourceDiscovery.discovery();
        statis.setServerList(serverList);
        return statis;
    }

    private void inspectTaskConfig(T taskConfig, TaskStatis statis) {
        String taskId = taskConfig.getTaskId();
        try {
            lock(taskId);
            TaskExecutorProxy<T> executorProxy = this.proxyHolder.getTaskExecutorProxy(taskId);
            if (taskConfig.isEffective() && executorProxy == null) {
                if (!TaskLicenseChecker.check(taskControlProperties.getTaskCode(), statis.getTaskServerMap().size() + 1))
                    return;// check license size
                ServerResource serverResource = balancer.balancer(taskConfig, statis, false);
                executorProxy = this.proxyHolder.createTaskExecutorProxy(taskConfig, serverResource);
                statis.putTaskServer(taskConfig.getTaskId(), serverResource.getId());
                executorProxy.start();
            }
            TComLogs.debug("inspect task [{} {}] config effective={}", getTaskControlProperties().getTaskCode(), taskConfig, taskConfig.isEffective());
        } catch (Throwable e) {
            TComLogs.error("inspect task [{} {}] error", e, getTaskControlProperties().getTaskCode(), taskConfig);
        } finally {
            unlock(taskId);
        }
    }

    private void inspectTaskExecutor(Proxy<T> proxy, TaskStatis statis) {
        TaskExecutorProxy<T> taskExecutor = proxy.taskProxy;
        String taskId = proxy.taskId;
        try {
            lock(taskId);
            T taskConfig = taskConfigProvider.getTaskConfig(taskId);
            int result = taskExecutor.inspect(taskConfig);
            if (result == 1) {
                this.proxyHolder.removeTaskExecutorProxy(taskId);
                statis.getTaskServerMap().remove(taskId);
                TComLogs.info("task executor [{}], task stopped, remove it", taskExecutor);
                taskExecutor.destory();
            }
            TComLogs.debug("inspect task executor [{}], result={}", taskExecutor, result);
        } catch (Throwable e) {
            TComLogs.error("inspect task executor [{}] error", e, taskExecutor);
        } finally {
            unlock(taskId);
        }
    }

    protected void lock(String taskId) {
        this.taskLock.lock(taskId);
    }

    protected void unlock(String taskId) {
        this.taskLock.unlock(taskId);
    }

    public TaskConfigProvider<T> getTaskConfigProvider() {
        return taskConfigProvider;
    }

    public void setTaskConfigProvider(TaskConfigProvider<T> taskConfigProvider) {
        this.taskConfigProvider = taskConfigProvider;
    }

    public void setServerResourceDiscovery(ServerResourceDiscovery srDiscovery) {
        this.serverResourceDiscovery = srDiscovery;
    }

    public void setAdjust(AdjustAlgorithm<T> adjust) {
        this.adjust = adjust;
    }

    public void setBalancer(BalanceAlgorithm<T> balancer) {
        this.balancer = balancer;
    }

    public TaskControlProperties getTaskControlProperties() {
        return taskControlProperties;
    }

    public void setTaskControlProperties(TaskControlProperties taskControlProperties) {
        this.taskControlProperties = taskControlProperties;
    }

    public synchronized void stop() {
        if (leader != null) {
            leader.stop();
        }
        destory();
    }

    public synchronized void start() {
        if (taskLock == null) {
            this.taskLock = new DTaskLock(taskControlProperties);
        }
        if (leader == null) {
            leader = new DTaskControlLeader();
            leader.setConfig(taskControlProperties);
            leader.setInspection(new LeaderInspection() {

                @Override
                public void inspect() {
                    TaskControl.this.inspect();
                }

                @Override
                public void init() {
                    TaskControl.this.init();
                }

                @Override
                public void destory() {
                    TaskControl.this.destory();
                }
            });
            leader.start();
        }
    }

    static interface LeaderInspection {

        /**
         * 执行一次巡检
         */
        public void inspect();

        /**
         * 初始化，可能会执行多次。在leader切换时都会进行一次初始化
         */
        public void init();

        /**
         * 当节点角色由leader转为非leader时执行destroy回调
         */
        public void destory();
    }

    static class DTaskControlLeader implements LeaderSelectorListener {

        private TaskControlProperties config;

        private LeaderSelector leaderSelector;

        private String leaderVoucher;

        private ScheduledFuture<?> scheduledFuture;

        private LeaderInspection inspection;

        public DTaskControlLeader() {
            this.leaderSelector = new LeaderSelector();
        }

        public void setConfig(TaskControlProperties config) {
            this.config = config;
        }

        public void setInspection(LeaderInspection inspection) {
            this.inspection = inspection;
        }

        public void start() {
            this.leaderVoucher = leaderSelector.issuedVoucher();
            TComLogs.info("[{}] issued voucher is {}", config.getTaskCode(), leaderVoucher);
            String leaderKey = String.format(Constants.KEY_FORMAT_LEADER, config.getTaskCode());
            this.leaderSelector.vote(leaderKey, leaderVoucher, this);
        }

        @Override
        public synchronized void voteSuccess(String voucher) {
            TComLogs.info("[{}] leader voucher is {}, self voucher is {}", config.getTaskCode(), voucher, leaderVoucher);
            boolean nowIsLeader = StringUtil.equals(voucher, leaderVoucher);
            if (nowIsLeader) {
                if (scheduledFuture == null) {
                    inspection.init();
                    scheduledFuture = TaskExecutors.submit(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                inspection.inspect();
                            } catch (Throwable e) {
                                TComLogs.error("", e);
                            }
                        }
                    }, 0, config.getPeriod(), TimeUnit.MILLISECONDS);
                }
            } else {
                if (scheduledFuture != null) {
                    try {
                        scheduledFuture.cancel(true);
                        scheduledFuture = null;
                    } catch (Throwable e) {
                        TComLogs.error("", e);
                    }
                    inspection.destory();
                }
            }
        }

        public synchronized void stop() {
            if (scheduledFuture != null) {
                scheduledFuture.cancel(true);
                scheduledFuture = null;
            }
        }
    }

    static class DTaskLock {

        private TaskControlProperties dtaskConfig;

        private Map<String, RandomDistributedReentrantLock> locks = new HashMap<String, RandomDistributedReentrantLock>();

        public DTaskLock(TaskControlProperties dtaskConfig) {
            super();
            this.dtaskConfig = dtaskConfig;
        }

        public void lock(String taskId) {
            getLock(taskId).lock();
        }

        public void unlock(String taskId) {
            getLock(taskId).unlock();
        }

        private synchronized RandomDistributedReentrantLock getLock(String taskId) {
            String lockKey = String.format(Constants.KEY_FORMAT_LOCK, dtaskConfig.getTaskCode(), taskId);
            RandomDistributedReentrantLock lock = locks.get(lockKey);
            if (lock == null) {
                lock = new RandomDistributedReentrantLock(lockKey);
                locks.put(lockKey, lock);
            }
            return lock;
        }
    }
}
