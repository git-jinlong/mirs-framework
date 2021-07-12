/**
 *
 */
package com.github.mirs.banxiaoxiao.framework.core.drpc.filter;

import com.github.mirs.banxiaoxiao.framework.core.error.BizException;
import com.github.mirs.banxiaoxiao.framework.core.exception.ServiceException;
import org.apache.dubbo.common.Constants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.common.utils.ReflectUtils;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.rpc.*;
import org.apache.dubbo.rpc.service.GenericService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * @author erxiao 2017年3月3日
 */
@Activate(group = Constants.PROVIDER)
public class DrpcExceptionFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(DrpcExceptionFilter.class);

    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        Result result = invoker.invoke(invocation);
        if (result.hasException() && GenericService.class != invoker.getInterface()) {
            try {
                Throwable exception = result.getException();
                boolean isBizException = (exception instanceof BizException);
                Class<?>[] exceptionClassses = null;
                try {
                    Method method = invoker.getInterface().getMethod(invocation.getMethodName(), invocation.getParameterTypes());
                    exceptionClassses = method.getExceptionTypes();
                } catch (NoSuchMethodException e) {
                }
                if (isBizException) {
                    return result;
                }
                // 如果是checked异常，直接抛出
                if (!(exception instanceof RuntimeException) && (exception instanceof Exception)) {
                    return result;
                }
                // 在方法签名上有声明，直接抛出
                if (exceptionClassses != null) {
                    for (Class<?> exceptionClass : exceptionClassses) {
                        if (exception.getClass().equals(exceptionClass)) {
                            return result;
                        }
                    }
                }
                // 异常类和接口类在同一jar包里，直接抛出
                String serviceFile = ReflectUtils.getCodeBase(invoker.getInterface());
                String exceptionFile = ReflectUtils.getCodeBase(exception.getClass());
                if (serviceFile == null || exceptionFile == null || serviceFile.equals(exceptionFile)) {
                    return result;
                }
                // 是JDK自带的异常，直接抛出
                String className = exception.getClass().getName();
                if (className.startsWith("java.") || className.startsWith("javax.")) {
                    return result;
                }
                // 如果是通用异常，则也抛出
                if (exception instanceof RpcException || exception instanceof ServiceException || exception instanceof BizException) {
                    return result;
                }
                // 否则，包装成RuntimeException抛给客户端
                return new RpcResult(new RuntimeException(StringUtils.toString(exception)));
            } catch (Throwable e) {
                logger.warn("Fail to DrpcExceptionFilter when called by " + RpcContext.getContext().getRemoteHost() + ". service: "
                        + invoker.getInterface().getName() + ", method: " + invocation.getMethodName() + ", exception: " + e.getClass().getName()
                        + ": " + e.getMessage(), e);
                return result;
            }
        }
        return result;
    }
}
