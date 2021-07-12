package com.github.mirs.banxiaoxiao.framework.common.shell;

/**
 * @author bc
 */
public class ShellException extends RuntimeException {

    public ShellException() {
    }

    public ShellException(String message) {
        super(message);
    }

    public ShellException(String message, Throwable cause) {
        super(message, cause);
    }

    public ShellException(Throwable cause) {
        super(cause);
    }
}
