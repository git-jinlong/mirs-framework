package com.github.mirs.banxiaoxiao.framework.core.error;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author erxiao
 *
 */
public class ExceptionUtils {

	/**
	 * 错误描述信息会将error的错误描述信息及参数message进行拼接作为新的错误描述信息
	 * 
	 * @param errorCode
	 * @param message
	 */
	public static void throwException(ErrorCode errorCode, String message) {
		throw new BizException(errorCode, message);
	}

	public static String getStackTraceMessage(Throwable exception) {
		if (exception == null) {
			return null;
		}
		String failMessage = null;
		try {
			ByteArrayOutputStream buf = new ByteArrayOutputStream();
			exception.printStackTrace(new java.io.PrintWriter(buf, true));
			failMessage = buf.toString();
			buf.close();
		} catch (IOException e1) {
			failMessage = exception.getMessage();
		}
		return failMessage;
	}
}
