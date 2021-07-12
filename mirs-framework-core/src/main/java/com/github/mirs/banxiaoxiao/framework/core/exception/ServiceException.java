package com.github.mirs.banxiaoxiao.framework.core.exception;


import com.github.mirs.banxiaoxiao.framework.core.status.Status;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;

/**
 * An ServiceException thrown when failed do some functionality. <p>The class contains an {@code
 * status} field that indicates specified error and contains specified {@code code} and {@code
 * message}.
 *
 * @author zw
 * @see Status
 */
public class ServiceException extends ApplicationException {

    private Status status;
    private Object[] args = ArrayUtils.EMPTY_OBJECT_ARRAY;

    public ServiceException(String msg) {
        super(msg);
    }

    public ServiceException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public ServiceException(Status status) {
        this(status.toString());
        this.status = status;
    }

    public ServiceException(Status status, Object... args) {
        this(status.toString() + ", args: " + Arrays.toString(args));
        this.status = status;
        this.args = args;
    }

    public ServiceException(Status status, Throwable cause) {
        this(status.toString(), cause);
        this.status = status;
    }

    public ServiceException(Status status, String message) {
        this(status.toString() + ", " + message);
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public Object[] getArgs() {
        return args;
    }
}
