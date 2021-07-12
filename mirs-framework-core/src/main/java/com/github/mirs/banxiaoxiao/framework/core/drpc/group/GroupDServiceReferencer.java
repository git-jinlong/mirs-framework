/**
 * 
 */
package com.github.mirs.banxiaoxiao.framework.core.drpc.group;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import com.github.mirs.banxiaoxiao.framework.core.drpc.spring.DReferenceBean;

/**
 * @author erxiao 2017年7月17日
 */
public class GroupDServiceReferencer<T> {

    DReferenceBean<?> referenceConfig;

    private Class<T> referenceClass;

    private String groupName;

    private T proxy;

    public GroupDServiceReferencer(Class<T> clazz, String groupName) {
        this.referenceClass = clazz;
        this.groupName = groupName;
    }

    private ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @SuppressWarnings("unchecked")
    public T referencer() {
        if (!referenceClass.isInterface()) {
            throw new IllegalStateException("The @Reference undefined interfaceClass or interfaceName, and the property type "
                    + referenceClass.getName() + " is not a interface.");
        }
        referenceConfig = new DReferenceBean<Object>();
        referenceConfig.setRpcConfig("lazy=true;filter=-exception,drpcException,drpcLog,drpcContext;group=" + groupName + ";interface=" + referenceClass.getName()
                + ";protocol=dubbo");
        if (applicationContext != null) {
            referenceConfig.setApplicationContext(applicationContext);
            try {
                referenceConfig.afterPropertiesSet();
            } catch (RuntimeException e) {
                throw (RuntimeException) e;
            } catch (Exception e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }
        proxy = (T) referenceConfig.get();
        return proxy;
    }

    public T get() {
        if (this.proxy == null) {
            return referencer();
        } else {
            return this.proxy;
        }
    }

    public void destroy() {
        if (this.referenceConfig != null) {
            this.referenceConfig.destroy();
        }
    }
}
