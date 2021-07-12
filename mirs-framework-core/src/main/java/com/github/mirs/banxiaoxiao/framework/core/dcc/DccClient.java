package com.github.mirs.banxiaoxiao.framework.core.dcc;

import com.github.mirs.banxiaoxiao.framework.core.config.BeeClientConfiguration;
import com.github.mirs.banxiaoxiao.framework.core.dcc.DccSessionHolder.HoldListener;
import com.github.mirs.banxiaoxiao.framework.core.log.TComLogs;
import com.github.mirs.banxiaoxiao.framework.common.util.StringUtil;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.List;

import static com.github.mirs.banxiaoxiao.framework.core.config.Constants.ZOOKEEPER_PASSWORD;
import static com.github.mirs.banxiaoxiao.framework.core.config.Constants.ZOOKEEPER_USERNAME;

/**
 * @author zcy 2018年8月23日
 */
/**
 * @param zkHostPort
 *            如：192.168.25.121:2181,192.168.25.122:2181,192.168.25.124:2181
 * @param connectTimeout
 * @throws IOException
 * @throws KeeperException
 * @throws InterruptedException
 */
/**
 * @author zcy 2018年8月23日
 */
public class DccClient {

    public static final String NODE_PATH_SEPARATOR = "/";

    private DccSessionHolder holder;

    private DccNodeWatcher nodeWatcher;

    private DccDataWatcher dataWatcher;

    private ZooKeeper zk;

    public DccClient(DccProperty dccProperty) throws IOException, KeeperException, InterruptedException {
        this(dccProperty.getZkHost(), dccProperty.getConnectTimeout());
    }

    public DccClient(String zkHostPort) throws IOException, KeeperException, InterruptedException {
        this(zkHostPort, 10000);
    }

    /**
     * @param zkHostPort
     *            如：192.168.25.121:2181,192.168.25.122:2181,192.168.25.124:2181
     * @param connectTimeout
     * @throws DccException
     */
    public DccClient(final String zkHostPort, int connectTimeout) throws DccException {
        this(zkHostPort,connectTimeout,BeeClientConfiguration.getLocalProperies().getProperty(ZOOKEEPER_USERNAME),BeeClientConfiguration.getLocalProperies().getProperty(ZOOKEEPER_PASSWORD));
    }

    /**
     * @param zkHostPort
     *            如：192.168.25.121:2181,192.168.25.122:2181,192.168.25.124:2181
     * @param connectTimeout
     * @throws DccException
     */
    public DccClient(final String zkHostPort, int connectTimeout,String username,String password) throws DccException {
        holder = new DccSessionHolder(zkHostPort, connectTimeout,username,password);

        zk = holder.connect();
        nodeWatcher = new DccNodeWatcher(zk);
        dataWatcher = new DccDataWatcher(zk);
        holder.registListener(new HoldListener() {

            @Override
            public void onReconnect(ZooKeeper zk) {

            }

            @Override
            public void onConnected(ZooKeeper zk) {
                DccClient.this.zk = zk;
                try{
                    dataWatcher.reconnect(zk);
                } catch(Exception e) {
                    TComLogs.error("", e);
                }
                nodeWatcher.reconnect(zk);
            }
        });
    }

    public ZooKeeper getZk() {
        return this.zk;
    }

    private String rightPath(String path) {
        if (StringUtil.isBlank(path)) {
            throw new DccRegistFailException("regist path be not null");
        }
        if (!path.startsWith(NODE_PATH_SEPARATOR)) {
            path = NODE_PATH_SEPARATOR + path;
        }
        return path;
    }

    /**
     * 注册数据监听器，如果同一个path重复监听，后注册的将覆盖前一个注册的监听器
     * 
     * @param c
     * @param path
     * @param listener
     * @return null
     */
    public <T> T registDataListener(Class<T> c, String path, DataListener<T> listener) {
        path = rightPath(path);
        return this.dataWatcher.regist(c, path, listener);
    }

    public void registNodeListener(String path, NodeListener nodeListener) {
        path = rightPath(path);
        this.nodeWatcher.regist(path, nodeListener);
    }

    public void removeDataListener(String path) {
        this.dataWatcher.removeDataListener(path);
    }

    public void removeDataListener(String path, DataListener<?> listener) {
        this.dataWatcher.removeDataListener(path, listener);
    }

    public void removeNodeListener(String path) {
        this.nodeWatcher.removeNodeListener(path);
    }

    public String readStringData(String path) {
        return readData(path, String.class);
    }

    public <T> T readData(String path, Class<T> c) {
        return this.dataWatcher.readData(path, c);
    }

    public <T> VersionData<T> readVersionData(String path, Class<T> c) {
        return this.dataWatcher.readVersionData(path, c);
    }

    public void writeData(String path, Object data) {
        path = rightPath(path);
        this.dataWatcher.write(path, data);
    }

    public int writeData(String path, Object data, int version) {
        path = rightPath(path);
        return this.dataWatcher.write(path, data, version);
    }

    public void writeTempData(String path, Object data) {
        path = rightPath(path);
        this.dataWatcher.writeTemp(path, data);
    }

    /**
     * 节点不存在时写入永久数据，否则不执行任何操作
     * 
     * @param path
     * @param data
     * @return 节点存在时返回false
     */
    public boolean writernx(String path, Object data) {
        path = rightPath(path);
        return this.dataWatcher.writernx(path, data);
    }

    public boolean isExist(String path) {
        try {
            return this.zk.exists(path, false) != null;
        } catch (Exception e) {
            throw new DccException(path, e);
        }
    }

    public String createTmpSeq(String path) {
        try {
            return this.zk.create(path, "".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        } catch (Exception e) {
            throw new DccException(path, e);
        }
    }

    /**
     * 节点不存在时写入临时数据，否则不执行任何操作
     * 
     * @param path
     * @param data
     * @return 节点存在时返回false
     */
    public boolean writernxTemp(String path, Object data) {
        path = rightPath(path);
        return this.dataWatcher.writernxTemp(path, data);
    }

    public void delete(String path) {
        path = rightPath(path);
        this.nodeWatcher.delete(path);
    }

    public List<String> getChildren(String path) {
        path = rightPath(path);
        return this.nodeWatcher.getChildren(path);
    }
}
