package com.github.mirs.banxiaoxiao.framework.core.woodpecker.vpipe;

import java.util.List;


/**
 * <pre>
 * 和CommandProcess组合成一个双向通信通道，CommandProcess用于woodpecker server向client执行指令和获取同步结果
 * CommandReporter用于woodpecker client主动向server上报数据或返回命令的异步结果
 * </pre>
 * @author zcy 2020年3月5日
 */
public interface MessageReporter {
    
    public List<String> report(Message msg);
}
