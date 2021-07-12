package com.github.mirs.banxiaoxiao.framework.core.drpc.router.rule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @类名: DccRuleCache
 * @描述: 静态方法提供
 * @作者: liudf
 * @日期: 2019/10/10 9:07
 */
@Component
public class DccRuleCache {

    private static DccRuleManager dccRuleManager;

    /**
     * 静态注入
     * @param dccRuleManager
     */
    @Autowired(required = false)
    public void setDccRuleManager(DccRuleManager dccRuleManager){
        DccRuleCache.dccRuleManager = dccRuleManager;
    }

    public static String find(String rule){
        return dccRuleManager.find(rule);
    }

    public static void add(String rule){
        dccRuleManager.add(rule);
    }

    public static void delete(){
        dccRuleManager.removeDccAll();
    }

    
    public static DccRuleManager getDccRuleManager() {
        return dccRuleManager;
    }

}
