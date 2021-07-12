package com.github.mirs.banxiaoxiao.framework.core.drpc.router.rule;

import com.github.mirs.banxiaoxiao.framework.common.util.NetworkUtil;
import com.github.mirs.banxiaoxiao.framework.core.dcc.conf.PushCacheDConfig;
import com.github.mirs.banxiaoxiao.framework.core.log.TComLogs;
import com.github.mirs.banxiaoxiao.framework.core.util.DataTimeWindow;
import com.github.mirs.banxiaoxiao.framework.core.util.DataTimeWindowListener;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @类名: DccRuleManager
 * @描述: dcc路由规则管理
 * @作者: liudf
 * @日期: 2019/10/9 17:15
 */
//@Component
public class DccRuleManager extends PushCacheDConfig<String> implements DataTimeWindowListener<String> {

    public static String ROOT_PATH = "/bee/router/rules";
    private static int outTime = 21600000;
    private DataTimeWindow<String> ruleDataWindow;

    /**
     * 获取新配置内容的时间间隔，单位毫秒
     * 配置一段时间内未被使用则移除，单位毫秒
     */
    public DccRuleManager() {
        super(ROOT_PATH, 10800000, outTime);
        super.setCheckOutime(outTime);
        ruleDataWindow = new DataTimeWindow<String>(TimeUnit.SECONDS, 300000, "ruleDataWindow", this);
    }

    /**
     * key: rule
     * value: host
     *
     * @param rule
     */
    public void add(String rule) {
        super.put(rule, NetworkUtil.getLocalHost());
        ruleDataWindow.pick(rule);
        ruleDataWindow.push(rule);
    }

    public void remove(String rule) {
        super.delete(rule);
    }

    /**
     * 内存不足时调用
     *
     * @param
     */
    public void removeAll() {
        List<String> rules = ruleDataWindow.gets();
        if (CollectionUtils.isNotEmpty(rules)) {
            rules.forEach(rule -> {
                TComLogs.info("memory less than 512M,remove router rule: {}", rule);
                ruleDataWindow.pick(rule);
                super.delete(rule);
            });
        }
    }

    /**
     * 不判断时间窗，直接移除zk数据
     */
    public void removeDccAll() {
        super.delete();
    }

    public String find(String rule) {
        //判断是否过期，过期则删除
        return super.find(rule);
    }

    @Override
    public void onInvalid(List<String> data) {
     /*   if (CollectionUtils.isNotEmpty(data)) {
            data.forEach(rule -> {
                super.delete(rule);
            });
        }*/
    }
}
