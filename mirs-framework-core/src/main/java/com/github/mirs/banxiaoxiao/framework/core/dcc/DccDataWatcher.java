package com.github.mirs.banxiaoxiao.framework.core.dcc;

import com.github.mirs.banxiaoxiao.framework.common.util.JsonUtils;
import com.github.mirs.banxiaoxiao.framework.common.util.StringUtil;
import com.github.mirs.banxiaoxiao.framework.core.log.TComLogs;
import org.apache.zookeeper.*;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zcy 2018年10月9日
 */
public class DccDataWatcher implements Watcher {

    private Logger logger = LoggerFactory.getLogger(DccDataWatcher.class);

    private Map<String, List<DataListenerItem>> dataListeners = new HashMap<String, List<DataListenerItem>>();

    private Map<String, Object> tempCache = new ConcurrentHashMap<String, Object>();

    private ZooKeeper zk;

    public DccDataWatcher(ZooKeeper zk) {
        this.zk = zk;
    }

    public synchronized void reconnect(ZooKeeper zk) {
        this.zk = zk;
        Iterator<Entry<String, Object>> itTemp = tempCache.entrySet().iterator();
        while (itTemp.hasNext()) {
            Entry<String, Object> entry = itTemp.next();
            String path = entry.getKey();
            Object data = entry.getValue();
            logger.debug("rewrite temp data {}:{}", path, data);
            writeTemp(path, data);
        }
        Iterator<Entry<String, List<DataListenerItem>>> itListener = dataListeners.entrySet().iterator();
        while (itListener.hasNext()) {
            Entry<String, List<DataListenerItem>> listener = itListener.next();
            String path = listener.getKey();
            noticeAndWatchData(path);
        }
    }

    @Override
    public synchronized void process(WatchedEvent event) {
        String path = event.getPath();
        if (event.getType() == Event.EventType.NodeDataChanged) {
            noticeAndWatchData(path);
        }
    }

    public synchronized <T> T regist(Class<T> c, String path, DataListener<T> listener) {
        List<DataListenerItem> listenerItems = this.dataListeners.get(path);
        if (listenerItems == null) {
            listenerItems = new ArrayList<DataListenerItem>();
            listenerItems.add(new DataListenerItem(listener, c));
            this.dataListeners.put(path, listenerItems);
            byte[] data = readAndWatchData(path);
            return toObject(c, data);
        } else if (!listenerItems.contains(listener)) {
            listenerItems.add(new DataListenerItem(listener, c));
            return readData(path, c);
        } else {
            // 重复注册返回null对象
            return null;
        }
    }

    public synchronized void removeDataListener(String path, DataListener<?> listener) {
        List<DataListenerItem> listenerItems = this.dataListeners.get(path);
        if (listenerItems == null) {
            return;
        }
        listenerItems.remove(new DataListenerItem(listener, null));
        if (listenerItems.size() == 0) {
            this.dataListeners.remove(path);
        }
    }

    public synchronized void removeDataListener(String path) {
        this.dataListeners.remove(path);
    }

    private byte[] readAndWatchData(String path) {
        try {
            return zk.getData(path, this, null);
        } catch (KeeperException e) {
            if (e.code() == KeeperException.Code.NONODE) {
                throw new NodeNotExistException(path, e);
            } else {
                throw new DccRegistFailException(path + " " + e.getMessage());
            }
        } catch (Throwable e) {
            throw new DccRegistFailException(path + " " + e.getMessage());
        }
    }

    public <T> T readData(String path, Class<T> c) {
        try {
            byte[] data = zk.getData(path, true, null);
            return toObject(c, data);
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

    public <T> VersionData<T> readVersionData(String path, Class<T> c) {
        try {
            Stat stat = new Stat();
            byte[] data = zk.getData(path, true, stat);
            T t = toObject(c, data);
            return new VersionData<T>(stat.getVersion(), t);
        } catch (KeeperException e) {
            if (e.code() == KeeperException.Code.NONODE) {
                return new VersionData<T>(-1, null);
            } else {
                throw new DccException(e);
            }
        } catch (InterruptedException e) {
            throw new DccException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T toObject(Class<T> c, byte[] data) {
        if (data == null) {
            return null;
        }
        String json = new String(data);
        if (String.class.equals(c) && !json.startsWith("\"")) {
            return (T) json;
        } else {
            return JsonUtils.fromJson(c, json);
        }
    }

    private byte[] toByte(Object obj) {
        if (obj == null) {
            return null;
        } else if (obj.getClass().equals(String.class)) {
            return obj.toString().getBytes();
        } else {
            return JsonUtils.toJson(obj).getBytes();
        }
    }

    private void noticeAndWatchData(String path) {
        List<DataListenerItem> listeners = this.dataListeners.get(path);
        if (listeners != null) {
            try {
                byte[] data = readAndWatchData(path);
                Iterator<DataListenerItem> iterator = listeners.iterator();
                while (iterator.hasNext()) {
                    DataListenerItem listenerItem = iterator.next();
                    notice(path, listenerItem, data);
                }
            } catch (NodeNotExistException e) {
                // 节点不存在，移除监听
                this.dataListeners.remove(path);
                TComLogs.warn("path {} not exist remove listeners", path);
            }
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void notice(String path, DataListenerItem listenerItem, byte[] data) {
        Class<?> clazz = listenerItem.getDataType();
        DataListener listener = listenerItem.getListener();
        Object obj = toObject(clazz, data);
        if (obj != null) {
            listener.onUpdate(path, obj);
        }
    }

    public void write(String path, Object data) {
        try {
            this.zk.setData(path, toByte(data), -1);
        } catch (KeeperException e) {
            if (e.code() == KeeperException.Code.NONODE) {
                createWhenNoNode(path, data);
            } else {
                throw new DccException(e);
            }
        } catch (InterruptedException e) {
            throw new DccException(e);
        }
    }

    public int write(String path, Object data, int version) {
        try {
            return this.zk.setData(path, toByte(data), version).getVersion();
        } catch (KeeperException e) {
            if (e.code() == KeeperException.Code.NONODE) {
                createWhenNoNode(path, data);
                return 0;
            } else if (e.code() == KeeperException.Code.BADVERSION) {
                throw new BadVersionException(e.getMessage());
            } else {
                throw new DccException(e);
            }
        } catch (InterruptedException e) {
            throw new DccException(e);
        }
    }

    /**
     * 写一个临时数据，在系统停止后数据将自动删除
     *
     * @param path
     * @param data
     */
    public void writeTemp(String path, Object data) {
        try {
            this.zk.setData(path, toByte(data), -1);
        } catch (KeeperException e) {
            if (e.code() == KeeperException.Code.NONODE) {
                createTempWhenNoNode(path, data);
            } else {
                throw new DccException(e);
            }
        } catch (InterruptedException e) {
            throw new DccException(e);
        }
        this.tempCache.put(path, data);
    }

    private void createWhenNoNode(String path, Object data) {
        if (path.contains(DccClient.NODE_PATH_SEPARATOR)) {
            String[] pathItems = path.split(DccClient.NODE_PATH_SEPARATOR);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < pathItems.length; i++) {
                String pathItem = pathItems[i];
                if (StringUtil.isBlank(pathItem)) {
                    continue;
                }
                Object wirteData = null;
                if (i == pathItems.length - 1) {
                    wirteData = data;
                }
                sb.append(DccClient.NODE_PATH_SEPARATOR).append(pathItem);
                try {
                    if ((this.zk.exists(sb.toString(), true)) == null) {
                        this.zk.create(sb.toString(), toByte(wirteData), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                    }
                } catch (KeeperException | InterruptedException e) {
                    throw new DccException(e);
                }
            }
        }
    }

    private void createTempWhenNoNode(String path, Object data) {
        if (path.contains(DccClient.NODE_PATH_SEPARATOR)) {
            String[] pathItems = path.split(DccClient.NODE_PATH_SEPARATOR);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < pathItems.length; i++) {
                String pathItem = pathItems[i];
                if (StringUtil.isBlank(pathItem)) {
                    continue;
                }
                Object wirteData = null;
                if (i == pathItems.length - 1) {
                    wirteData = data;
                }
                sb.append(DccClient.NODE_PATH_SEPARATOR).append(pathItem);
                try {
                    if ((this.zk.exists(sb.toString(), true)) == null) {
                        if (i == pathItems.length - 1) {
                            this.zk.create(sb.toString(), toByte(wirteData), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                        } else {
                            this.zk.create(sb.toString(), toByte(wirteData), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                        }
                    }
                } catch (KeeperException e) {
                    if (e.code() == KeeperException.Code.NODEEXISTS) {
                        continue;
                    } else {
                        throw new DccException(path + " " + e.getMessage());
                    }
                } catch (Throwable e) {
                    throw new DccException(e);
                }
            }
        }
    }

    /**
     * 节点不存在时写入永久数据，否则不执行任何操作
     *
     * @param path
     * @param data
     * @return 节点存在时返回false
     */
    public boolean writernx(String path, Object data) {
        try {
            this.zk.create(path, toByte(data), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        } catch (KeeperException e) {
            if (e.code() == KeeperException.Code.NODEEXISTS) {
                return false;
            } else {
                throw new DccException(e);
            }
        } catch (InterruptedException e) {
            throw new DccException(e);
        }
        return true;
    }

    /**
     * 节点不存在时写入临时数据，否则不执行任何操作
     *
     * @param path
     * @param data
     * @return 节点存在时返回false
     */
    public boolean writernxTemp(String path, Object data) {
        try {
            this.zk.create(path, toByte(data), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        } catch (KeeperException e) {
            if (e.code() == KeeperException.Code.NODEEXISTS) {
                return false;
            } else {
                throw new DccException(e);
            }
        } catch (InterruptedException e) {
            throw new DccException(e);
        }
        return true;
    }

    static class DataListenerItem {

        private DataListener<?> listener;

        private Class<?> dataType;

        public DataListenerItem(DataListener<?> listener, Class<?> dataType) {
            this.listener = listener;
            this.dataType = dataType;
        }

        public DataListener<?> getListener() {
            return listener;
        }

        public void setListener(DataListener<?> listener) {
            this.listener = listener;
        }

        public Class<?> getDataType() {
            return dataType;
        }

        public void setDataType(Class<?> dataType) {
            this.dataType = dataType;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof DataListener) {
                return getListener().equals((DataListener<?>) obj);
            } else if (obj instanceof DataListenerItem) {
                return getListener().equals(((DataListenerItem) obj).getListener());
            }
            return false;
        }
    }
}
