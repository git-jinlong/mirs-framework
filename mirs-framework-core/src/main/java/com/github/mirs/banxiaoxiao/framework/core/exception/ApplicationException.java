package com.github.mirs.banxiaoxiao.framework.core.exception;

/**
 * A base class for extends. All of exception should be extend this class.
 *
 * @author zw
 */
public class ApplicationException extends RuntimeException {

  public ApplicationException(String message) {
    super(message);
  }

  public ApplicationException(String message, Throwable cause) {
    super(message, cause);
  }

  public ApplicationException(Throwable cause) {
    super(cause);
  }

}
