package com.github.mirs.banxiaoxiao.framework.sftp.exception;

/**
 * @author chenying
 * @since 2019-08-20
 */
public class FileCreateException extends RuntimeException {

    private String msg;
    private int code = 500;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public FileCreateException(String message) {
        super(message);
    }

    public FileCreateException(String msg, Throwable e) {
        super(msg, e);
    }
}
