package com.github.mirs.banxiaoxiao.framework.core.dcc.autoval;

import java.util.List;

import com.github.mirs.banxiaoxiao.framework.core.dcc.DataListener;
import com.github.mirs.banxiaoxiao.framework.core.dcc.DccClient;
import com.github.mirs.banxiaoxiao.framework.core.dcc.NodeListener;
import com.github.mirs.banxiaoxiao.framework.core.dcc.SingleDccClientHelper;
import com.github.mirs.banxiaoxiao.framework.core.log.TComLogs;
import com.github.mirs.banxiaoxiao.framework.common.util.StringUtil;

/**
 * @author zcy 2018年9月17日
 */
public abstract class BaseDccValSpace extends ValSpace implements DataListener<String>, NodeListener {

    /** */
    private static final long serialVersionUID = 4983167951250302754L;

    private String namespaceRoot;

    private DccClient dccClient;

    public BaseDccValSpace() {
    }

    public BaseDccValSpace(DccClient dccClient) {
        super();
        this.dccClient = dccClient;
    }

    public DccClient getDccClient() {
        DccClient dccClient = this.dccClient;
        if (dccClient == null) {
            dccClient = SingleDccClientHelper.get();
            this.dccClient = dccClient;
        }
        if (dccClient == null) {
            throw new IllegalArgumentException("dcc client not found");
        }
        return dccClient;
    }

    public void setDccClient(DccClient dccClient) {
        this.dccClient = dccClient;
    }

    public void load() {
        this.namespaceRoot = getNameSpacePath();
        this.dccClient.writeData(this.namespaceRoot, "");
        registerDcc();
        initLocalVals();
    }

    protected void initLocalVals() {
    }

    protected void registerDcc() {
        this.dccClient.registNodeListener(this.namespaceRoot, this);
        List<String> children = this.dccClient.getChildren(namespaceRoot);
        if (children != null) {
            onRefreshChildren(children);
        }
    }

    protected String getNameSpacePath() {
        return Vals.ROOT_PATH.concat("/").concat(getNamespace());
    }

    protected String genValPath(String key) {
        return Vals.ROOT_PATH.concat("/").concat(getNamespace()).concat("/").concat(key);
    }

    public Object put(Object key, Object value) {
        super.put(key, value);
        String path = genValPath((String) key);
        getDccClient().writeData(path, value);
        return value;
    }

    @Override
    public void onUpdate(String path, String data) {
        String key = path;
        int index = path.lastIndexOf("/");
        if (index > 0) {
            key = path.substring(index + 1);
        }
        Object existData = get(key);
        if (!StringUtil.equals((existData == null ? null : existData.toString()), data)) {
            super.put(key, data);
        }
    }

    @Override
    public void onDestroy(String nodeName) {
    }

    @Override
    public void onConstruct(String nodeName) {
    }

    @Override
    public void onRefreshChildren(List<String> children) {
        for (String child : children) {
            try {
                String val = this.dccClient.registDataListener(String.class, genValPath(child), this);
                if (val == null) {
                    val = "";
                }
                Object exist = super.get(child);
                if (!val.equals(exist)) {
                    super.put(child, val);
                }
            } catch(Exception e) {
                TComLogs.error("registDataListener fail {}", e, child);
            }
        }
    }
}
