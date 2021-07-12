package com.github.mirs.banxiaoxiao.framework.core.dcc.ochannel;

import java.util.ArrayList;
import java.util.List;

import com.github.mirs.banxiaoxiao.framework.core.dcc.DataListener;
import com.github.mirs.banxiaoxiao.framework.core.dcc.VersionData;
import com.github.mirs.banxiaoxiao.framework.core.log.TComLogs;
import com.github.mirs.banxiaoxiao.framework.common.util.StringUtil;

/**
 * @author zcy 2019年3月18日
 * @param <S>
 */
public class OneWayChannelClient<S> extends DccOneWayChannel<S> implements DataListener<S> {

    private List<SignalReceiver<S>> receivers = new ArrayList<SignalReceiver<S>>();

    public OneWayChannelClient(String id, Class<S> clazz) {
        super(id);
        setClazz(clazz);
    }

    @Override
    public void open() throws ChannelNotExistException {
        try {
            super.open();
        } catch (ChannelNotExistException e) {
            getDccClient().writeTempData(getChannelPath(), null);
            super.open();
        }
        notifyReceiveData();
        registerListener();
    }
    
    @SuppressWarnings("unchecked")
    protected void registerListener() {
        getDccClient().registDataListener((Class<S>)getClazz(), getChannelPath(), this);
    }

    protected void notifyReceiveData() {
        VersionData<S> s = readVersionData(getChannelPath());
        S data = s.getData();
        if(String.class.equals(getClazz()) && StringUtil.isBlank((String) data)) {
            data = null;
        }
        if (data != null) {
            writeData(getChannelPath(), "", s.getVersion());
            notifyReceiver(data);
        }
    }

    protected void notifyReceiver(S data) {
        if (this.receivers != null) {
            for (SignalReceiver<S> receiver : this.receivers) {
                try { 
                    receiver.onSignal(data);
                } catch (Exception e) {
                    TComLogs.error("notify [{}] signal fail", e, receiver);
                }
            }
        }
    }

    public void addReceiver(SignalReceiver<S> receiver) {
        if (this.receivers == null) {
            this.receivers = new ArrayList<>();
        }
        if (!this.receivers.contains(receiver)) {
            this.receivers.add(receiver);
        }
    }

    @Override
    public void onUpdate(String path, S data) {
        notifyReceiveData();
    }
}
