package com.github.mirs.banxiaoxiao.framework.core.error.v1;

import java.io.Serializable;

import com.github.mirs.banxiaoxiao.framework.core.error.berd.BerdErrorCode;
import com.github.mirs.banxiaoxiao.framework.common.util.StringUtil;

/**
 * 第一代版本错误码
 * 
 * @author erxiao
 *
 */
public class V1ErrorCode implements BerdErrorCode, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2144014939503066082L;

	/**
	 * 错误码前缀，固定值:AE
	 */
	public static final char[] CODE_PRE = { 'A', 'E' };

	/**
	 * 错误码版本
	 */
	public static final char[] VERSION = { '0', '1' };

	private char errorType;

	private char errorLevel;

	private char[] systemCode;

	private char[] systemErrorCode;

	private ErrorLevel errorLevelEnum;

	private ErrorType errorTypeEnum;

	private String assembleCode;

	public static final String ERROR_TYPE_SERVICE = "0";

	public static final String ERROR_TYPE_SYSTEM = "1";

	public static final String ERROR_LEVEL_INFO = "0";

	public static final String ERROR_LEVEL_WARN = "1";

	public static final String ERROR_LEVEL_ERROR = "2";

	public static final String ERROR_LEVEL_CRITICAL = "3";

	private String message;
	
	public V1ErrorCode() {
	}

	/**
	 * @param errorType
	 * @param errorLevel
	 * @param systemCode
	 * @param systemErrorCode
	 */
	public V1ErrorCode(ErrorType errorType, ErrorLevel errorLevel, String systemCode, String systemErrorCode, String message) {
		assembleCode(errorType, errorLevel, systemCode, systemErrorCode);
		this.message = message;
	}

	/**
	 * 组装错误码
	 * 
	 * @return
	 */
	private String assembleCode() {
		StringBuilder builder = new StringBuilder();
		builder.append(CODE_PRE).append(VERSION);
		builder.append(errorType).append(errorLevel);
		builder.append(systemCode).append(systemErrorCode);
		return builder.toString();
	}

	@Override
	public String getCode() {
		return this.assembleCode;
	}

	public void setCode(String code) {
		if (code == null || code.length() != 13) {
			throw new IllegalArgumentException("not support error code." + code);
		}
		String version = code.substring(2, 4);
		String type = code.substring(4, 5);
		String level = code.substring(5, 6);
		String systemCodeStr = code.substring(6, 9);
		String errorCodeStr = code.substring(9);
		if (version.equals("01")) {
			ErrorType errorTypeEnum = type.equals(V1ErrorCode.ERROR_TYPE_SERVICE) ? ErrorType.SERVICE : ErrorType.SYSTEM;
			ErrorLevel errorLevelEnum = ErrorLevel.ERROR;
			if (level.equals(V1ErrorCode.ERROR_LEVEL_INFO)) {
				errorLevelEnum = ErrorLevel.INFO;
			} else if (level.equals(V1ErrorCode.ERROR_LEVEL_WARN)) {
				errorLevelEnum = ErrorLevel.WARN;
			} else if (level.equals(V1ErrorCode.ERROR_LEVEL_ERROR)) {
				errorLevelEnum = ErrorLevel.ERROR;
			} else if (level.equals(V1ErrorCode.ERROR_LEVEL_CRITICAL)) {
				errorLevelEnum = ErrorLevel.CRITICAL;
			}
			assembleCode(errorTypeEnum, errorLevelEnum, systemCodeStr, errorCodeStr);
		} else {
			throw new IllegalArgumentException("not support code." + code);
		}
	}

	private void assembleCode(ErrorType errorType, ErrorLevel errorLevel, String systemCode, String systemErrorCode) {
		switch (errorType) {
		case SERVICE:
			this.errorType = '0';
			break;
		case SYSTEM:
			this.errorType = '1';
			break;
		default:
			throw new IllegalArgumentException(String.format("Error Code[v%s] type[%s] not support.", String.valueOf(V1ErrorCode.VERSION), errorType));
		}
		switch (errorLevel) {
		case INFO:
			this.errorLevel = '0';
			break;
		case WARN:
			this.errorLevel = '1';
			break;
		case ERROR:
			this.errorLevel = '2';
			break;
		case CRITICAL:
			this.errorLevel = '3';
			break;
		default:
			throw new IllegalArgumentException(String.format("Error Code[v%s] level[%s] not support.", String.valueOf(V1ErrorCode.VERSION),
					errorLevel));
		}
		if (StringUtil.trim(systemCode).length() != 3) {
			throw new IllegalArgumentException(String.format("Error Code[v%s] system code[%s] must be 3 length.",
					String.valueOf(V1ErrorCode.VERSION), systemCode));
		}
		if (StringUtil.trim(systemErrorCode).length() != 4) {
			throw new IllegalArgumentException(String.format("Error Code[v%s] system error code[%s] must be 4 length.",
					String.valueOf(V1ErrorCode.VERSION), systemErrorCode));
		}
		this.systemCode = systemCode.toCharArray();
		this.systemErrorCode = systemErrorCode.toCharArray();
		this.assembleCode = assembleCode();
		this.errorLevelEnum = errorLevel;
		this.errorTypeEnum = errorType;
	}

	public ErrorLevel getErrorLevel() {
		return this.errorLevelEnum;
	}

	public ErrorType getErrorTypeEnum() {
		return errorTypeEnum;
	}

	@Override
	public String getMessage() {
	    return this.message;
	}
}
