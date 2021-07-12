package com.github.mirs.banxiaoxiao.framework.core.dcc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import com.github.mirs.banxiaoxiao.framework.common.util.StringUtil;

/**
 * @author zcy 2018年10月9日
 */
public class DccNodeWatcher implements Watcher {

    private Map<String, List<NodeListener>> nodeListeners = new ConcurrentHashMap<String, List<NodeListener>>();

    private ZooKeeper zk;

    public DccNodeWatcher(ZooKeeper zk) {
        this.zk = zk;
    }

    public void reconnect(ZooKeeper zk) {
        this.zk = zk;
        for (String path : nodeListeners.keySet()) {
            List<String> children = focusWatch(path);
            notifyChildrenChanage(path, children);
        }
    }

    @Override
    public void process(WatchedEvent event) {
        String path = event.getPath();
        List<String> children = null;
        switch (event.getType()) {
        case NodeCreated:
        case NodeDeleted:
            children = focusWatch(path);
            judgeNodeEventAndNotify(path);
            break;
        case NodeChildrenChanged:
            children = focusWatch(path);
            notifyChildrenChanage(path, children);
            break;
        default:
            break;
        }
    }

    public synchronized void regist(String path, NodeListener nodeListener) {
        List<NodeListener> listeners = nodeListeners.get(path);
        if (listeners == null) {
            listeners = new ArrayList<NodeListener>();
            listeners.add(nodeListener);
            this.nodeListeners.put(path, listeners);
            focusWatch(path);
        } else if (!listeners.contains(nodeListener)) {
            listeners.add(nodeListener);
        }
    }

    public synchronized void removeNodeListener(String path) {
        this.nodeListeners.remove(path);
    }

    public synchronized void removeNodeListener(String path, NodeListener nodeListener) {
        List<NodeListener> listeners = this.nodeListeners.get(path);
        if (listeners == null) {
            return;
        }
        listeners.remove(nodeListener);
        if (listeners.size() == 0) {
            this.nodeListeners.remove(path);
        }
    }

    public void delete(String path) {
        try {
            this.zk.delete(path, -1);
        } catch (KeeperException e) {
            if (e.code() == KeeperException.Code.NONODE) {
            } else {
                throw new DccException(e);
            }
        } catch (InterruptedException e) {
            throw new DccException(e);
        }
    }

    public List<String> getChildren(String path) {
        try {
            return this.zk.getChildren(path, false);
        } catch (KeeperException e) {
            if (e.code() == KeeperException.Code.NONODE) {
                return null;
            } else {
                throw new DccException(e);
            }
        } catch (InterruptedException e) {
            throw new DccException(e);
        }
    }

    private List<String> focusWatch(String path) {
        try {
            if (this.zk.exists(path, this) != null) {
                return this.zk.getChildren(path, this);
            }
        } catch (KeeperException | InterruptedException e) {
            throw new DccException(path + " " + e.getMessage());
        }
        return null;
    }

    private synchronized void judgeNodeEventAndNotify(String path) {
        List<NodeListener> listeners = nodeListeners.get(path);
        if (listeners != null) {
            Stat stat = null;
            String nodeName = rightChindPath(path);
            if (!StringUtil.isBlank(path)) {
                try {
                    stat = this.zk.exists(path, false);
                } catch (KeeperException | InterruptedException e) {
                    throw new DccException(path + " " + e.getMessage());
                }
            }
            List<NodeListener> backListeners = new ArrayList<NodeListener>(listeners); 
            //back 一下在通知，主要是在删除节点的情况下，上层业务监听器可能会移除从而导致ConcurrentModificationException错误
            //因为推送给所有的监听器、上层业务监听器移除自身的监听器都是zk的通知线程执行的即使加锁了，也被重入了
            Iterator<NodeListener> iterator = backListeners.iterator();
            while(iterator.hasNext()) {
                NodeListener listener = iterator.next();
                if (stat == null) {
                    listener.onDestroy(nodeName);
                } else {
                    listener.onConstruct(nodeName);
                }
            }
        }
    }

    private synchronized  List<String> notifyChildrenChanage(String path, List<String> children) {
        List<NodeListener> listeners = nodeListeners.get(path);
        if (listeners != null) {
            for (NodeListener listener : listeners) {
                listener.onRefreshChildren(children);
            }
        }
        return null;
    }

    private String rightChindPath(String path) {
        if (path.startsWith(DccClient.NODE_PATH_SEPARATOR)) {
            return path.substring(1);
        } else {
            return path;
        }
    }
}
