package com.github.mirs.banxiaoxiao.framework.core.dcc.conf;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.github.mirs.banxiaoxiao.framework.core.dcc.AbstractDccApp;
import com.github.mirs.banxiaoxiao.framework.core.dcc.DataListener;
import com.github.mirs.banxiaoxiao.framework.core.dcc.DccClient;
import com.github.mirs.banxiaoxiao.framework.core.dcc.NodeListener;
import com.github.mirs.banxiaoxiao.framework.core.log.TComLogs;

/**
 * @author zcy 2019年3月12日
 * @param <v>
 */
public abstract class BaseDConfig<v> extends AbstractDccApp implements DConfig<v>, DataListener<v>, NodeListener {

    transient private List<DConfigListener<v>> listeners;

    transient private boolean listenData = false;
    
    transient private List<String> lastChanageChildren = new ArrayList<String>();
    
    public BaseDConfig(String root) {
        this(root, null);
    }

    public BaseDConfig(String root, DccClient dccClient) {
        super(root, dccClient, null);
        Class<?> slefClazz = getClass();
        while (slefClazz != Object.class) {
            Type t = slefClazz.getGenericSuperclass();
            if (t instanceof ParameterizedType) {
                Type[] args = ((ParameterizedType) t).getActualTypeArguments();
                if (args[0] instanceof Class) {
                    setClazz( (Class<?>) args[0]);
                    break;
                }
            }
            slefClazz = slefClazz.getSuperclass();
        }
    }

    /**
     * <pre>
     * 开启配置项配置内容变更监听，开启后如果配置项内容发生变化会回调 以下方法
     * {@link DConfigListener#onConfigUpdate(String, Object)}
     * 开启监听后暂不支持取消监听
     * </pre>
     */
    public void startChildDataListen() {
        this.listenData = true;
    }

    /**
     * <pre>
     * 开启配置项集合发生变化监听，开启后，如果新增/删除配置项（新增/删除 key）会回调方法：
     * {@link DConfigListener#onConfigCreate(String, Object)}
     * {@link DConfigListener#onDelete(String, Object)}
     * {@link DConfigListener#onChildrenChanage(List)}
     * 注意：如果该配置项已存在（key存在）只是更新内容，不会触发{@link DConfigListener#onChildrenChanage(List)}推送
     * 开启监听后暂不支持取消监听
     * </pre>
     */
    public void startChildListListen() {
        getDccClient().registNodeListener(getRoot(), this);
        List<String> children = all();
        onRefreshChildren(children);
    }

    @Override
    public List<String> all() {
        return getDccClient().getChildren(getRoot());
    }

    @Override
    public v find(String key) {
        String path = genChildPath(key);
        return readData(path);
    }

    @Override
    public boolean exist(String key) {
        return find(key) != null;
    }

    @Override
    public void put(String key, v conf) {
        String path = genChildPath(key);
        getDccClient().writeData(path, conf);
        if (listenData) {
            registDataListener(key);
        }
    }

    public void delete() {
        List<String> children = all();
        if (children != null) {
            for (String child : children) {
                delete(child);
            }
        }
    }

    @Override
    public boolean delete(String key) {
        String path = genChildPath(key);
        getDccClient().delete(path);
        if (listenData) {
            removeDataListener(key);
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    protected v registDataListener(String key) {
        String path = genChildPath(key);
        return getDccClient().registDataListener((Class<v>)getClazz(), path, this);
    }

    protected void removeDataListener(String key) {
        String path = genChildPath(key);
        getDccClient().removeDataListener(path, this);
    }

    @Override
    public void onDestroy(String nodeName) {
        notifyDelete(nodeName);
    }

    @Override
    public void onConstruct(String nodeName) {
        String path = genChildPath(nodeName);
        v config = readData(path);
        if (config != null) {
            notifyCreate(nodeName, config);
        }
    }

    @Override
    public void onRefreshChildren(List<String> children) {
        if (children != null) {
            if (listenData) {
                for (String child : children) {
                    try {
                        registDataListener(child);
                    } catch(Exception e) {
                        TComLogs.error("registDataListener fail {}", e, child);
                    }
                }
            }
        }
        notifyChildren(children);
    }

    @Override
    public void onUpdate(String path, v data) {
        String key = pickLeafPath(path);
        notifyUpdate(key, data);
    }

    @Override
    public void addDConfigListener(DConfigListener<v> listener) {
        if (this.listeners == null) {
            this.listeners = new ArrayList<DConfigListener<v>>();
        }
        this.listeners.add(listener);
    }

    protected synchronized void notifyChildren(List<String> children) {
        if (this.listeners != null && children != null) {
            for (DConfigListener<v> listener : listeners) {
                try {
                    listener.onChildrenChanage(children);
                } catch (Exception e) {
                    TComLogs.error("notify listener [{}] to config children chanage event, size={}  error", listener,
                            children == null ? 0 : children.size());
                }
            }
            for(String key : children) {
                if(!lastChanageChildren.contains(key)) {
                    notifyCreate(key, find(key));
                }
            }
            
            for(String key : lastChanageChildren) {
                if(!children.contains(key)) {
                    notifyDelete(key);
                }
            }
        }
        if(children == null) {
            this.lastChanageChildren = new ArrayList<String>();
        } else {
            this.lastChanageChildren = children;
        }
    }

    protected void notifyCreate(String key, v config) {
        if (this.listeners != null) {
            for (DConfigListener<v> listener : listeners) {
                try {
                    listener.onConfigCreate(key, config);
                } catch (Exception e) {
                    TComLogs.error("notify listener [{}] to config create event [{}, {}]  error", listener, key, config);
                }
            }
        }
    }

    protected void notifyDelete(String key) {
        if (this.listeners != null) {
            for (DConfigListener<v> listener : listeners) {
                try {
                    listener.onConfigDelete(key);
                } catch (Exception e) {
                    TComLogs.error("notify listener [{}] to config delete event [{}]  error", listener, key);
                }
            }
        }
    }

    protected void notifyUpdate(String key, v config) {
        if (this.listeners != null) {
            for (DConfigListener<v> listener : listeners) {
                try {
                    listener.onConfigUpdate(key, config);
                } catch (Exception e) {
                    TComLogs.error("notify listener [{}] to config update event [{}]  error", listener, config);
                }
            }
        }
    }
}
