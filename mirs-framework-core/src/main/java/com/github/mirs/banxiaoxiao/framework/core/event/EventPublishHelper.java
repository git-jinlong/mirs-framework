package com.github.mirs.banxiaoxiao.framework.core.event;

/**
 * @author zcy 2019年1月17日
 */
public class EventPublishHelper extends EventPublisher {

    private EventPublishHelper() {
    }

    private static class SingletonContainer {

        private static EventPublishHelper INSTANCE = new EventPublishHelper();
    }  

    public static EventPublishHelper get() {
        return SingletonContainer.INSTANCE;
    }
}
