package com.github.mirs.banxiaoxiao.framework.rabbitmq.p2p.router.rule;

import com.github.mirs.banxiaoxiao.framework.rabbitmq.mq.p2p.P2PClient;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @类名: P2pConsistentHashRouter
 * @描述: 一致性Hash路由P2P方式
 * @作者: liudf
 * @日期: 2021/4/19 13:39
 */
public class P2pConsistentHashRouter {

    private List<String> clients;

    private Map<String,P2PClient> clientsMap = new ConcurrentHashMap<>();

    private final ConcurrentMap<String, ConsistentHashSelector> selectors = new ConcurrentHashMap<String, ConsistentHashSelector>();

    public P2pConsistentHashRouter() {

    }

    /**
     * 虚拟节点的数目，一个真实结点对应512个虚拟节点
     */
    private static final int VIRTUAL_NODES = 512;

    private TreeMap<Long, String> virtualInvokers;

    public void init(List<P2PClient> p2pClients) {
        this.clients = new ArrayList<>();
        for (int i = 0; i < p2pClients.size(); i++) {
            this.clients.add(p2pClients.get(i).getEventKey());
            this.clientsMap.put(p2pClients.get(i).getEventKey(),p2pClients.get(i));
        }

        virtualInvokers = new TreeMap<Long, String>();
        for (String address : clients) {
            for (int i = 0; i < VIRTUAL_NODES; i++) {
                long m = hash(address + "&&VN" + i);
                virtualInvokers.put(m, address);
            }
        }
    }

    //此处可以优化，摄像机分片数据量达到几十万时，首次会耗时几百ms
    public P2PClient doSelect(String key) {
        if (CollectionUtils.isEmpty(clients)){
            return null;
        }
        if (clients.size() == 1){
            return clientsMap.get(clients.get(0));
        }
        int identityHashCode = System.identityHashCode(clients);
        ConsistentHashSelector selector = selectors.get(key);
        if (selector == null || selector.identityHashCode != identityHashCode) {
            selectors.put(key, new ConsistentHashSelector(identityHashCode));
            selector = selectors.get(key);
        }
        String eventKey = selector.select(key);
        return clientsMap.get(eventKey);
    }

    private long hash(String nodeName) {
        final int p = 16777619;
        int hash = (int) 2166136261L;
        for (int i = 0; i < nodeName.length(); i++) {
            hash = (hash ^ nodeName.charAt(i)) * p;
        }
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;
        //如果算出来的值为负数则取其绝对值
        if (hash < 0) {
            hash = Math.abs(hash);
        }
        return hash;
    }

    private class ConsistentHashSelector{

        private final int identityHashCode;

        ConsistentHashSelector(int identityHashCode) {
            this.identityHashCode = identityHashCode;
        }

        public String select(String key) {
            return selectForKey(hash(key));
        }

        private String selectForKey(long hash) {
            Map.Entry<Long, String> entry = virtualInvokers.ceilingEntry(hash);
            if (entry == null) {
                entry = virtualInvokers.firstEntry();
            }
            String value = entry.getValue();
            return value;
        }
    }

}
