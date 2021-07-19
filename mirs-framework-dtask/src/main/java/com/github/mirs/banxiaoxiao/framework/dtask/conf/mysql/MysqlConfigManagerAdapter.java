package com.github.mirs.banxiaoxiao.framework.dtask.conf.mysql;

import com.github.mirs.banxiaoxiao.framework.dtask.TaskException;
import com.github.mirs.banxiaoxiao.framework.dtask.conf.TaskConfig;
import com.github.mirs.banxiaoxiao.framework.dtask.conf.TaskConfigProvider;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zcy 2019年5月29日
 * @param <T>
 */
public class MysqlConfigManagerAdapter<T extends TaskConfig> implements TaskConfigProvider<T> {

    private Object mysqlDelegate;

    private Map<String, Method> targetMethods;

    public MysqlConfigManagerAdapter(Object delegate) {
        check(delegate);
        initTargetMethods();
        this.mysqlDelegate = delegate;
    }

    private void check(Object delegate){
        for(Method method : TaskConfigProvider.class.getMethods()){
            Method delegateMethod = null;
            try {
                delegateMethod = delegate.getClass().getMethod(method.getName(),method.getParameterTypes());
            } catch (NoSuchMethodException | SecurityException exception) {
            }
            if(delegateMethod == null){
                throw new NotFoundTargetConfigMethod("please implements the method "+method);
            }
        }
    }

    private void initTargetMethods(){
        this.targetMethods = new HashMap<>();
        for(Method method : TaskConfigProvider.class.getMethods()){
            this.targetMethods.put(method.getName(),method);
        }
    }

    public void setMysqlDelegate(Object delegate){
        check(delegate);
        initTargetMethods();
        this.mysqlDelegate = delegate;
    }

    public Object getMysqlDelegate(){
        return mysqlDelegate;
    }

    private Object invoke(String methodName, Object... args) {
        Method method = targetMethods.get(methodName);
        if (method == null) {
            throw new TaskException(new NoSuchMethodException(methodName));
        }
        try {
            return method.invoke(this.mysqlDelegate, args);
        } catch (Exception e) {
            throw new TaskException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getTaskConfig(String taskId) {
        return (T) invoke("getTaskConfig", taskId);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<T> getTaskConfigs(int pageNum) {
        return (List<T>) invoke("getTaskConfigs", pageNum);
    }

    @Override
    public boolean isExist(String taskId) {
        return (boolean) invoke("isExist", taskId);
    }

    public static void main(String[] args){
        for(Method method : TaskConfigProvider.class.getMethods()){
            System.out.println(method.getName());
        }
    }
}
