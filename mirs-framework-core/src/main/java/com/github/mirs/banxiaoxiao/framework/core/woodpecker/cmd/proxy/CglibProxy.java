package com.github.mirs.banxiaoxiao.framework.core.woodpecker.cmd.proxy;

import java.lang.reflect.Method;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import com.github.mirs.banxiaoxiao.framework.core.woodpecker.cmd.bean.BeanManager;

public class CglibProxy implements MethodInterceptor {

    public Object newInstall(Object object) {
        return Enhancer.create(object.getClass(), this);
    }

    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        System.out.println("先热身一会");
        methodProxy.invokeSuper(o, objects);
        System.out.println("打完了");
        return null;
    }
    
    public static void main(String args[]) {
        CglibProxy p = new CglibProxy();
        BeanManager o = (BeanManager)p.newInstall(BeanManager.getInstance());
        System.out.println(o.getAll());
        
    }
}