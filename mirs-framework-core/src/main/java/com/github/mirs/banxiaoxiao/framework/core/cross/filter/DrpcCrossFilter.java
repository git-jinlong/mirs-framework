/**
 *
 */
package com.github.mirs.banxiaoxiao.framework.core.cross.filter;

import com.github.mirs.banxiaoxiao.framework.core.cross.CDSResultProxy;
import com.github.mirs.banxiaoxiao.framework.core.cross.CDSResultProxyFactroy;
import com.github.mirs.banxiaoxiao.framework.core.cross.annotation.CDS;
import com.github.mirs.banxiaoxiao.framework.core.cross.config.CrossFilterMapCacheHelper;
import com.github.mirs.banxiaoxiao.framework.core.cross.enums.TipEnum;
import com.github.mirs.banxiaoxiao.framework.core.log.TComLogs;
import com.github.mirs.banxiaoxiao.framework.core.spring.SpringContextHolder;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.Constants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import org.springframework.beans.BeansException;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Objects;

/**
 * @author lxj
 */
@Activate(group = Constants.PROVIDER, order = 2)
public class DrpcCrossFilter implements Filter {

    private List<String> cacheAnnotationType = Lists.newArrayList();

    /**
     * (non-Javadoc)
     *
     * @see Filter#invoke(Invoker, Invocation)
     */
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        //本地的GenericService发起的rpc直接进行返回
        Object requestId = RpcContext.getContext().getAttachment(invoker.getInterface().getName().concat("_").concat(invocation.getMethodName()));
        if (requestId != null && StringUtils.equals(requestId.toString(), TipEnum.RESP.name())) {
            return invoker.invoke(invocation);
        }

        //本地通过CDS发起的请求需要转入三网模块
        try {
            CDSResultProxy cdsResultProxy = CDSResultProxyFactroy.getInstance();
            if (Objects.isNull(cdsResultProxy)) {
                return invoker.invoke(invocation);
            }
            if (cacheAnnotationType.contains(invoker.getInterface().getName())) {
                return cdsResultProxy.invoke(invoker, invocation);//三网要做的
            }
            Object obj = CrossFilterMapCacheHelper.get(invoker.getInterface().getName());
            if (Objects.isNull(obj)) {
                obj = SpringContextHolder.get().getBean(invoker.getInterface());
                CrossFilterMapCacheHelper.put(invoker.getInterface().getName(), obj);
            }
            if (StringUtils.equals(obj.toString(), "DrpcCrossFilter BeansException") || StringUtils.equals(obj.toString(), "DrpcCrossFilter RpcException")) {
                return invoker.invoke(invocation);
            }
            Annotation[] annotations = obj.getClass().getAnnotationsByType(CDS.class);
            if (annotations.length > 0) {
                cacheAnnotationType.add(invoker.getInterface().getName());
                return cdsResultProxy.invoke(invoker, invocation);//三网要做的
            } else {
                return invoker.invoke(invocation);
            }
        } catch (RpcException e) {
            TComLogs.info("DrpcCrossFilter RpcException");
            CrossFilterMapCacheHelper.put(invoker.getInterface().getName(), "DrpcCrossFilter RpcException");
            e.printStackTrace();
        } catch (BeansException e) {
            TComLogs.info("DrpcCrossFilter BeansException");
            CrossFilterMapCacheHelper.put(invoker.getInterface().getName(), "DrpcCrossFilter BeansException");
            e.printStackTrace();
        }
        return invoker.invoke(invocation);
    }

}
