package com.github.mirs.banxiaoxiao.framework.rabbitmq.p2p.router;

import com.github.mirs.banxiaoxiao.framework.core.log.TComLogs;
import com.github.mirs.banxiaoxiao.framework.core.util.DataTimeWindow;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.RmqException;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.p2p.AbstractMsgClientListener;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.p2p.P2PRouter;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.p2p.consult.ConsultationDeskHelper;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 周期定向p2p算法，在一定时间周期内将具有相同key的数据发送到相同的client
 *
 * @author zcy 2019年7月19日
 */
public class CycleTimeDirRouter extends AbstractMsgClientListener implements P2PRouter {

    private DataTimeWindow<MsgKeyBinding> timeWindow;

    private Random rand = new Random(200);

    public CycleTimeDirRouter(Class<?> msgType, TimeUnit unit, int length) {
        super(msgType);
        timeWindow = new DataTimeWindow<MsgKeyBinding>(unit, length, "CycleTimeDirRouter");
        ConsultationDeskHelper.addClientListener(this);
    }

    @Override
    public String choose(Object msg) {
        if (!(msg instanceof MsgKeyHandler)) {
            throw new RmqException("message [" + msg + "] must implement interface " + MsgKeyHandler.class.getName());
        }
        String key = ((MsgKeyHandler) msg).getKey();
        MsgKeyBinding binding = new MsgKeyBinding(key);
        MsgKeyBinding exist = timeWindow.pick(binding);
        List<String> clientIds = getClients();
        String chooseClient = null;
        if (exist != null) {
            TComLogs.debug("key={},exist [{}]", key, exist);
            if (!clientIds.contains(exist.getTargetClient())) {
                TComLogs.debug("client {} expiry, random again", exist.getTargetClient());
                chooseClient = choose(msg);
            } else {
                // 重新放入时间窗
                timeWindow.push(exist);
                chooseClient = exist.getTargetClient();
            }
        } else if (clientIds != null) {
            if (preIndex == -1) {
                chooseClient = randomP2PClient(clientIds);
            } else {
                chooseClient = pollP2PClient(clientIds);
            }
            if (chooseClient != null) {
                binding.setTargetClient(chooseClient);
                timeWindow.push(binding);
            }
        }
        return chooseClient;
    }

    private int preIndex = -1;

    private String pollP2PClient(List<String> clientIds) {
        if (clientIds == null || clientIds.size() == 0) {
            return null;
        }
        int index = preIndex;
        if (index >= (clientIds.size() - 1)) {
            index = 0;
        } else {
            index = index + 1;
        }
        String chooseClient = clientIds.get(index);
        preIndex = index;
        return chooseClient;
    }

    private String randomP2PClient(List<String> clientIds) {
        int length = clientIds.size();
        int randIndex = rand.nextInt(length);
        String chooseClient = clientIds.get(randIndex);
        preIndex = randIndex;
        return chooseClient;
    }

    static class MsgKeyBinding {

        private String key;

        private String targetClient;

        public MsgKeyBinding(String key) {
            super();
            this.key = key;
        }

        public MsgKeyBinding(String key, String targetClient) {
            super();
            this.key = key;
            this.targetClient = targetClient;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getTargetClient() {
            return targetClient;
        }

        public void setTargetClient(String targetClient) {
            this.targetClient = targetClient;
        }

        @Override
        public int hashCode() {
            return (getKey() + getTargetClient()).hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof MsgKeyBinding)) {
                return false;
            }
            MsgKeyBinding temp = (MsgKeyBinding) obj;
            if (key == null || temp.getKey() == null) {
                return false;
            }
            return key.equals(temp.getKey());
        }

        @Override
        public String toString() {
            return getKey() + "->" + getTargetClient();
        }
    }
}
