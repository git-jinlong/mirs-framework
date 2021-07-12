package com.github.mirs.banxiaoxiao.framework.core.dcc.ochannel;

import com.github.mirs.banxiaoxiao.framework.core.dcc.VersionData;

/**
 * @author zcy 2019年3月18日
 * @param <S>
 */
public class OneWayChannelServer<S> extends DccOneWayChannel<S> {

    public OneWayChannelServer(String id) {
        super(id);
    }

    public void send(S data, int version) throws ChannelException {
        VersionData<S> s = readVersionData(getChannelPath());
        if (s.getData() != null) {
            throw new SignalNotConsumeException(id() + " unconsumed signaling exists in channel");
        }
        super.send(data, version);
    }
}
