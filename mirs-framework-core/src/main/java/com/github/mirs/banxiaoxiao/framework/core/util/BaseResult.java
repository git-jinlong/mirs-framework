package com.github.mirs.banxiaoxiao.framework.core.util;

import java.io.Serializable;

/**
 * @author zcy 2018年10月8日
 */
public class BaseResult implements Serializable {

    /** */
    private static final long serialVersionUID = 737327502607041662L;

    public static final int RESULT_SUCCESS = 0;

    public static final int RESULT_FAIL = 1;

    private int resultCode = RESULT_SUCCESS;

    private String message;

    public BaseResult() {
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return this.resultCode == RESULT_SUCCESS;
    }
}
