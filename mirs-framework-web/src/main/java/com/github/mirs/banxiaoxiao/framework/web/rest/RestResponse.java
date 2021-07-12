package com.github.mirs.banxiaoxiao.framework.web.rest;


import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * A class defines structure for serialize or de-serialize result for http.
 *
 * @author zw
 */
public class RestResponse {

  private int code = 0;
  private String message = "";

  public RestResponse() {
  }

  public RestResponse(int code) {
    this.code = code;
  }

  public RestResponse(int code, String message) {
    this(code);
    this.message = message;
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
  }

  public boolean isSuccess() {
    return code == 0;
  }
}
