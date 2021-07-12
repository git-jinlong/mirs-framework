package com.github.mirs.banxiaoxiao.framework.core.cross;

import com.github.mirs.banxiaoxiao.framework.core.log.TComLogs;
import com.github.mirs.banxiaoxiao.framework.core.spring.SpringContextHolder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @Auther: lxj
 * @Date: 2020/2/28 14:34
 * @Description:
 */
@Component
public class CDSResultProxyFactroy implements InitializingBean {

    private static CDSResultProxy cdsResultProxy;

    public static CDSResultProxy getInstance() {
        return cdsResultProxy;
    }

    @Override
    public void afterPropertiesSet() {

        try {
            CDSResultProxy cdsResultProxy_ = SpringContextHolder.get().getBean(CDSResultProxy.class);
            if (Objects.nonNull(cdsResultProxy_)) {
                cdsResultProxy = cdsResultProxy_;
            }
        } catch (BeansException e) {
            TComLogs.info("can not find CDSResultProxy impl. ");
        }
    }
}
