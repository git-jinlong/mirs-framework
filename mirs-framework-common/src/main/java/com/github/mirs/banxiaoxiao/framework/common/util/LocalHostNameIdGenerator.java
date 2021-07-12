package com.github.mirs.banxiaoxiao.framework.common.util;

/**
 * @author zcy 2018年9月30日
 */
public class LocalHostNameIdGenerator extends SnowflakeIdGenerator {

    private static int workId = 0;

    static {
        String hostName = NetworkUtil.getHostName();
        String lastNum = "00";
        if (hostName.length() > 0 && hostName.length() < 2) {
            lastNum = hostName;
        } else {
            lastNum = hostName.substring(hostName.length() - 2);
        }
        String workerIdStr = StringUtil.alignLeft(lastNum, 2, "0");
        try {
            workId = Integer.parseInt(workerIdStr);
        } catch (Exception e) {
            workId = workerIdStr.hashCode();
        }
    }

    public LocalHostNameIdGenerator() {
        // 取机器名最后两位作为workerId
        this(workId, 0);
    }

    public LocalHostNameIdGenerator(long workerId, long dataCenterId) {
        super(workerId, dataCenterId);
    }

    private static class SingletonContainer {

        private static LocalHostNameIdGenerator INSTANCE = new LocalHostNameIdGenerator();
    }

    public static LocalHostNameIdGenerator singleton() {
        return SingletonContainer.INSTANCE;
    }
}
