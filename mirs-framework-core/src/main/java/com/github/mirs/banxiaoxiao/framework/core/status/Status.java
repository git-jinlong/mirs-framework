package com.github.mirs.banxiaoxiao.framework.core.status;

/**
 * A {@code Status} indicates the result of operation. <p><b>Implementation notes: </b>You shouldn't
 * use {@code 0-20000} as code because those code will use for commons code.
 *
 * @author zw
 */
public interface Status {

    /**
     * The code represent to status.
     *
     * @return the code of status
     */
    int getCode();

    /**
     * The message represent to status.
     *
     * @return the message
     */
    String getMessage();

}
