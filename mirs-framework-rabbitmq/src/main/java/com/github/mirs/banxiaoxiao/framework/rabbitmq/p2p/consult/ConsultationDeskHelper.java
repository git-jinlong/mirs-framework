package com.github.mirs.banxiaoxiao.framework.rabbitmq.p2p.consult;

import com.github.mirs.banxiaoxiao.framework.rabbitmq.p2p.P2PClientListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zcy 2019年7月23日
 */
public class ConsultationDeskHelper {

    private static ConsultationDesk INSTANCE;

    private static List<P2PClientListener> listeners = new ArrayList<P2PClientListener>();

    public static ConsultationDesk get() {
        if (INSTANCE == null) {
            throw new NullPointerException("consultation desk not instantiated, please add @RmqEnable");
        }
        return INSTANCE;
    }

    public synchronized static void set(ConsultationDesk desk) {
        if (desk == null) {
            throw new NullPointerException("consultation desk can not be null.");
        }
        INSTANCE = desk;
        if (listeners.size() > 0) {
            for (P2PClientListener listener : listeners) {
                INSTANCE.addP2PClientListener(listener);
            }
            listeners.clear();
        }
    }

    public synchronized static void addClientListener(P2PClientListener listener) {
        if (INSTANCE == null) {
            listeners.add(listener);
        } else {
            INSTANCE.addP2PClientListener(listener);
        }
    }
}
