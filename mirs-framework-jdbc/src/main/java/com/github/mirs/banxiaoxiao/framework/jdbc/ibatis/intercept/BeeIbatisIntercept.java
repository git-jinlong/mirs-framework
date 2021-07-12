package com.github.mirs.banxiaoxiao.framework.jdbc.ibatis.intercept;

import com.github.mirs.banxiaoxiao.framework.jdbc.ibatis.intercept.annotation.Order;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.util.Properties;

/**
 * @Auther: lxj
 * @Date: 2020/7/7 10:29
 * @Description:
 */
@Order
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class BeeIbatisIntercept implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
//        Long startTime = System.currentTimeMillis();
//        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
//        String sql = mappedStatement.getBoundSql(invocation.getArgs()[1]).getSql();
//        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
//        SqlResolverHelper.verify(sql,sqlCommandType);
//        TComLogs.debug("耗时:{}",System.currentTimeMillis()-startTime);
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
