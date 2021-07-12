package com.github.mirs.banxiaoxiao.framework.core.drpc.router;

import com.github.mirs.banxiaoxiao.framework.core.drpc.router.rule.DccRuleCache;
import com.github.mirs.banxiaoxiao.framework.core.log.TComLogs;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.Router;

import java.util.ArrayList;
import java.util.List;

import static com.github.mirs.banxiaoxiao.framework.core.config.Constants.DRPC_DUBBO_ROUTER_ADDRESS;
import static com.github.mirs.banxiaoxiao.framework.core.config.Constants.DRPC_DUBBO_ROUTER_KEY;

/**
 * @类名: DynamicSetRouter
 * @描述: 动态参数路由实现
 * @作者: liudf
 * @日期: 2019/9/20 10:13
 */
public class DynamicRouter implements Router {

//    public static ConcurrentHashMap<String,String> ruleMap = new ConcurrentHashMap<>();

    private URL url;

    public DynamicRouter(URL url) {
        this.url = url;
    }

    @Override
    public URL getUrl() {
        return url;
    }

    /**
     * Filter invokers with current routing rule and only return the invokers that comply with the rule.
     *
     * @param invokers   invoker list
     * @param url        refer url
     * @param invocation invocation
     * @return routed invokers
     * @throws RpcException
     */
    @Override
    public <T> List<Invoker<T>> route(List<Invoker<T>> invokers, URL url, Invocation invocation) throws RpcException {
        if (CollectionUtils.isNotEmpty(invokers)){
            if (invokers.size() == 1){
                //单机模式
                return invokers;
            }
            String routerAddress = invocation.getAttachment(DRPC_DUBBO_ROUTER_ADDRESS);
            if (StringUtils.isNotEmpty(routerAddress)){
                for (int i = 0; i < invokers.size(); i++) {
                    Invoker<T> invoker = invokers.get(i);
                    String providerHost = invokers.get(i).getUrl().getHost();
                    if (providerHost.equals(routerAddress)){
                        TComLogs.debug("routerAddress,routerAddress={}",routerAddress);
                        List<Invoker<T>> result = new ArrayList<Invoker<T>>();
                        result.add(invoker);
                        return result;
                    }
                }
            }
            String ruleKey = invocation.getAttachment(DRPC_DUBBO_ROUTER_KEY);
            if (StringUtils.isEmpty(ruleKey)){
                return invokers;
            }
            String consumerHost = DccRuleCache.find(ruleKey);
            if (StringUtils.isEmpty(consumerHost)){
                return invokers;
            }
            for (int i = 0; i < invokers.size(); i++) {
                Invoker<T> invoker = invokers.get(i);
                String providerHost = invokers.get(i).getUrl().getHost();
                if (providerHost.equals(consumerHost)){
                    TComLogs.debug("route ruleKey={},providerHost={},consumerHost={}",ruleKey,providerHost,consumerHost);
                    List<Invoker<T>> result = new ArrayList<Invoker<T>>();
                    result.add(invoker);
                    return result;
                }
            }
            // 有路由信息，但是不在invoker列表里，更新历史路由表
            List<String> keys = DccRuleCache.getDccRuleManager().all();
            if(keys != null) {
                for(String key : keys) {
                    String host = DccRuleCache.find(key);
                    if (consumerHost.equals(host)){
                        DccRuleCache.getDccRuleManager().delete(key);
                    }
                }
            }
        }
        return invokers;
    }

    @Override
    public boolean isRuntime() {
        return true;
    }

    @Override
    public boolean isForce() {
        return false;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public <T> void notify(List<Invoker<T>> invokers) {
//        super.notify();
    }

}
