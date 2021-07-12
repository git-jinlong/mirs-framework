package com.github.mirs.banxiaoxiao.framework.core.event;

import com.github.mirs.banxiaoxiao.framework.core.exception.ServiceException;

/**
 * <pre>
 * 业务拦截器，一个业务操作完成后，通过EventPublisher发布业务时间，其他业务模块需要的时候进行业务拦截并进行相应的处理.
 * 如果操作不合理可以抛出异常终止前一环节的业务操作。
 * 和 {@link EventListener}的区别是，EventListener只是监听，不会打断业务操作，而{@link BizInterceptor}可以打断
 * </pre>
 *
 * @author zcy 2020年7月2日
 */
public interface BizInterceptor<E> {

    /**
     * @param e
     * @throws ServiceException
     */
    public Object interceptor(E e) throws ServiceException;
}
