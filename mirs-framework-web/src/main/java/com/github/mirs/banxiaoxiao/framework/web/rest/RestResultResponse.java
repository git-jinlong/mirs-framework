package com.github.mirs.banxiaoxiao.framework.web.rest;


/**
 * A response that can contains an type for result property.
 *
 * @author zw
 */
public class RestResultResponse<T> extends RestResponse {

  private T result;

  public RestResultResponse() {
  }

  public RestResultResponse(int code, String message) {
    super(code, message);
  }

  public T getResult() {
    return result;
  }

  public void setResult(T result) {
    this.result = result;
  }

}
