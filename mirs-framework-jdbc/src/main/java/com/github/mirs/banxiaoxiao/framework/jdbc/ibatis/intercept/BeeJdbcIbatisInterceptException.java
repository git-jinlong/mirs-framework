package com.github.mirs.banxiaoxiao.framework.jdbc.ibatis.intercept;

/**
 * @Auther: lxj
 * @Date: 2020/7/7 10:31
 * @Description:
 */
public class BeeJdbcIbatisInterceptException extends RuntimeException{

    public BeeJdbcIbatisInterceptException() {
    }

    public BeeJdbcIbatisInterceptException(String message) {
        super(message);
    }

    public BeeJdbcIbatisInterceptException(String message, Throwable cause) {
        super(message, cause);
    }

    public BeeJdbcIbatisInterceptException(Throwable cause) {
        super(cause);
    }

    public BeeJdbcIbatisInterceptException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
