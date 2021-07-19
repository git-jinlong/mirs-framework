package com.github.mirs.banxiaoxiao.framework.rabbitmq.p2p;

import com.github.mirs.banxiaoxiao.framework.rabbitmq.p2p.consult.P2PHeartbeat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zcy 2019年7月19日
 */
public abstract class AbstractMsgClientListener implements P2PClientListener {

    private Map<String, List<String>> clients = new ConcurrentHashMap<String, List<String>>();

    private Class<?> msgType;

    public AbstractMsgClientListener(Class<?> msgType) {
        this.msgType = msgType;
    }

    public Class<?> getMsgType() {
        return this.msgType;
    }

    @Override
    public void onHeartbeat(List<P2PHeartbeat> heartbeatClients) {
        if (heartbeatClients != null) {
            for (P2PHeartbeat client : heartbeatClients) {
                List<String> supportTypes = client.getSupportMsgTypes();
                if (supportTypes != null && client.getHost() != null) {
                    for (String supportType : supportTypes) {
                        List<String> supportClients = clients.get(supportType);
                        if (supportClients == null) {
                            supportClients = new ArrayList<String>();
                            clients.put(supportType, supportClients);
                        }
                        if (!supportClients.contains(client.getHost())) {
                            supportClients.add(client.getHost());
                        }
                        if (supportClients.size() > 1){
                            supportClients.removeIf(host -> !heartbeatClients.contains(new P2PHeartbeat(host)));
                        }
                    }
                }
            }
        }
        List<String> clients = getClients();
        processHeartbeat(clients);
    }
    
    protected void processHeartbeat(List<String> clients) {
        // 留给子类扩展处理
    }

    public List<String> getClients() {
        return getClients(getMsgType().getName());
    }
    
    public List<String> getClients(String msgTypeName) {
        return clients.get(msgTypeName);
    }

    public Map<String, List<String>> getClientMap() {
        return clients;
    }
}
