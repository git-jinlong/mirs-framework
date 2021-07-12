package com.github.mirs.banxiaoxiao.framework.core.monitor;

/**
 * @author zcy 2019年1月17日
 */
@Deprecated
public class EventPublishHelper extends EventPublisher {

    private EventPublishHelper() {
    }

    private static class SingletonContainer {

        private static EventPublishHelper INSTANCE = new EventPublishHelper();
    }  

    public static EventPublishHelper single() {
        return SingletonContainer.INSTANCE;
    }
}
