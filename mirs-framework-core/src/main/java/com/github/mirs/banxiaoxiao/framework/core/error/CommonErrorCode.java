package com.github.mirs.banxiaoxiao.framework.core.error;

import com.github.mirs.banxiaoxiao.framework.core.error.berd.BerdErrorCode;
import com.github.mirs.banxiaoxiao.framework.core.error.v1.ErrorLevel;
import com.github.mirs.banxiaoxiao.framework.core.error.v1.ErrorType;
import com.github.mirs.banxiaoxiao.framework.core.error.v1.V1ErrorCode;

/**
 * 第一代版本错误码
 * 
 * @author erxiao
 *
 */
public enum CommonErrorCode implements BerdErrorCode {

	/** 未知错误 */
	UNKONWN(ErrorType.SYSTEM, ErrorLevel.CRITICAL, "000", "0000", "未知错误"),

	/** 无效参数异常错误 */
	ILLEGAL_ARGUMENT_EXCEPTION(ErrorType.SYSTEM, ErrorLevel.ERROR, "000", "0001", "无效参数异常错误"),

	SERVICE_TIMEOUT(ErrorType.SYSTEM, ErrorLevel.ERROR, "000", "0002", "服务调用超时"),
	
	SERVICE_BUSY(ErrorType.SERVICE, ErrorLevel.WARN, "000", "0003", "服务繁忙，处理失败");

	private ErrorCode error;

	CommonErrorCode(ErrorType errorType, ErrorLevel errorLevel, String systemCode, String systemErrorCode, String message) {
		this.error = new V1ErrorCode(errorType, errorLevel, systemCode, systemErrorCode, message);
	}

	@Override
	public String getCode() {
		return this.error.getCode();
	}

	/**
	 * @return the errorMessage
	 */
	public String getMessage() {
		return error.getMessage();
	}

	public boolean equalsTo(ErrorCode ec) {
		return getCode().equals(ec.getCode());
	}

}
