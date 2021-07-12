package com.github.mirs.banxiaoxiao.framework.core.woodpecker.vpipe;

import java.util.List;

/**
 * @author zcy 2019年6月4日
 */
public interface CommandProcess {

    public List<String> invoke(Command command);
}
