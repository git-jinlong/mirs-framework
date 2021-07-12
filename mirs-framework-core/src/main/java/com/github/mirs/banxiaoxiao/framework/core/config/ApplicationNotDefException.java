/**
 * 
 */
package com.github.mirs.banxiaoxiao.framework.core.config;

/**
 * @author erxiao 2017年2月14日
 */
public class ApplicationNotDefException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5710547566668805242L;

	public ApplicationNotDefException() {
	}

	public ApplicationNotDefException(String appName) {
		super(appName);
	}

	public ApplicationNotDefException(Throwable e) {
		super(e);
	}

	public ApplicationNotDefException(String msg, Throwable e) {
		super(msg, e);
	}
}
