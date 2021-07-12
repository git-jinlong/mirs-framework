package com.github.mirs.banxiaoxiao.framework.core.dcc.ochannel;

import com.github.mirs.banxiaoxiao.framework.core.dcc.AbstractDccApp;
import com.github.mirs.banxiaoxiao.framework.core.dcc.BadVersionException;

/**
 * 
 * @author zcy 2019年3月13日
 */
public abstract class DccOneWayChannel<S> extends AbstractDccApp implements OneWayChannel<S> {

    private static final String PATH_ROOT = "channel";

    private String channelPath;

    private String id;

    private volatile boolean opened = false;

    public DccOneWayChannel(String id) {
        super(PATH_ROOT);
        this.id = id;
        this.channelPath = genChildPath(id);
    }

    @Override
    public String id() {
        return id;
    }

    public boolean isOpened() {
        return opened;
    }

    public String getChannelPath() {
        return channelPath;
    }

    @Override
    public void open() throws ChannelNotExistException {
        boolean isExist = getDccClient().isExist(this.channelPath);
        if (!isExist) {
            throw new ChannelNotExistException("the channel not exist");
        }
        opened = true;
    }

    @Override
    public void send(S data, int version) throws ChannelException {
        if (!isOpened()) {
            throw new ChannelNotOpenException(this.id);
        }
        try {
            writeData(this.channelPath, data, version);
        } catch (BadVersionException badVersion) {
            throw new ChannelDataOverwritedException(this.id);
        } catch (Exception e) {
            throw new ChannelException("unknown exception", e);
        }
    }

    @Override
    public void setQos(int qos) {
        throw new UnsupportedOperationException("unsupported qos");
    }

    @Override
    public void destory() {
        getDccClient().delete(this.channelPath);
        this.opened = false;
    }
}
