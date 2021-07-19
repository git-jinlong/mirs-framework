package com.github.mirs.banxiaoxiao.framework.dtask.conf.mix;

import com.github.mirs.banxiaoxiao.framework.common.util.TaskExecutors;
import com.github.mirs.banxiaoxiao.framework.core.dcc.conf.SimpleDConfig;
import com.github.mirs.banxiaoxiao.framework.core.log.TComLogs;
import com.github.mirs.banxiaoxiao.framework.dtask.conf.TaskConfig;
import com.github.mirs.banxiaoxiao.framework.dtask.conf.TaskConfigProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @param <T>
 * @author zcy 2019年5月29日
 */
public class MixTaskConfigProvider<T extends TaskConfig> implements TaskConfigProvider<T> {

    private ScheduledFuture<?> scheduledFuture;

    private SimpleDConfig<T> dccConfig;

    private TaskConfigProvider<T> mysqlConfig;

    /*private MysqlConfigManagerAdapter<T> mysqlAdapterConfig;

    public MixTaskConfigProvider(SimpleDConfig<T> dccConfig,Object mysqlDelegate){
        this.dccConfig = dccConfig;
        this.mysqlAdapterConfig = new MysqlConfigManagerAdapter<T>(mysqlDelegate);
    }*/

    public MixTaskConfigProvider(SimpleDConfig<T> dccConfig, TaskConfigProvider<T> mysqlConfig) {
        this.dccConfig = dccConfig;
        this.mysqlConfig = mysqlConfig;
    }

    @Override
    public boolean isExist(String taskId) {
        return dccConfig.exist(taskId);
    }

    @Override
    public T getTaskConfig(String taskId) {
        return dccConfig.find(taskId);
    }

    @Override
    public List<T> getTaskConfigs(int pageNum) {
        if (pageNum == 0) {
            List<String> all = dccConfig.all();
            if (all != null && all.size() > 0) {
                List<T> configs = new ArrayList<T>();
                for (String configKey : all) {
                    T config = dccConfig.find(configKey);
                    if (config != null) {
                        configs.add(config);
                    }
                }
                return configs;
            }
        }
        return null;
    }

    public void stopSync() {
        if (scheduledFuture != null) {
            try {
                scheduledFuture.cancel(true);
                scheduledFuture = null;
            } catch (Throwable e) {
                TComLogs.error("Mix provider stop synchronize config error.", e);
            }
        }
    }

    public void startSync() {
        if (scheduledFuture == null) {
            scheduledFuture = TaskExecutors.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        syncDbToDcc();
                    } catch (Throwable e) {
                        TComLogs.error("Mix provider synchronize config error.", e);
                    }
                }
            }, 0, 10, TimeUnit.SECONDS);
        }
    }

    private synchronized void syncDbToDcc() {
        List<T> configsDb = new ArrayList<>();
        int dbPageNum = 0;
        while (true) {
            List<T> configsDbPage = this.mysqlConfig.getTaskConfigs(dbPageNum);
            dbPageNum++;
            if (configsDbPage == null || configsDbPage.size() == 0) {
                break;
            }
            configsDb.addAll(configsDbPage);
        }

        List<T> configsDcc = new ArrayList<>();
        int dccPageNum = 0;
        while (true) {
            List<T> configsDccPage = this.getTaskConfigs(dccPageNum);
            dccPageNum++;
            if (configsDccPage == null || configsDccPage.size() == 0) {
                break;
            }
            configsDcc.addAll(configsDccPage);
        }
        TComLogs.debug("Mix config provider synchronize db to dcc. db config size : {}, dcc config size : {}", configsDb.size(), configsDcc.size());

        Map<String, Integer> elementCount = new HashMap<>();
        for (T taskConfig : configsDb) {
            if (taskConfig == null) continue;
            elementCount.put(taskConfig.getTaskId(), 1);
        }
        for (T taskConfig : configsDcc) {
            if (elementCount.containsKey(taskConfig.getTaskId())) {
                elementCount.put(taskConfig.getTaskId(), 2);
            } else {
                TComLogs.info("Mix config provider synchronize,to delete task id:{}", taskConfig.getTaskId());
                dccConfig.delete(taskConfig.getTaskId());
            }
        }
        for (T taskConfig : configsDb) {
            if (taskConfig == null) continue;
            String taskId = taskConfig.getTaskId();
            if (elementCount.get(taskId).equals(1)) {
                TComLogs.info("Mix config provider synchronize,to add task id:{}", taskId);
                dccConfig.put(taskId, taskConfig);
            } else if (elementCount.get(taskId).equals(2)) {
                if (taskConfig.getLastUpdateTime() > configsDcc.get(configsDcc.indexOf(taskConfig)).getLastUpdateTime()) {
                    TComLogs.info("Mix config provider synchronize,to update task id:{}", taskId);
                    dccConfig.put(taskId, taskConfig);
                }
            }
        }
        elementCount.clear();
        configsDb.clear();
        configsDcc.clear();
    }

    public TaskConfigProvider<T> getMysqlConfig() {
        return mysqlConfig;
    }

    public void setMysqlConfig(TaskConfigProvider<T> mysqlConfig) {
        this.mysqlConfig = mysqlConfig;
    }

    /*public void setMysqlDelegate(Object mysqlDelegate){
        this.mysqlAdapterConfig.setMysqlDelegate(mysqlDelegate);
    }

    public Object getMysqlDelegate(){
        return this.mysqlAdapterConfig.getMysqlDelegate();
    }*/
    public static void main(String[] args) {
        List<String> result = new ArrayList<>();
        result.clear();
    }
}
