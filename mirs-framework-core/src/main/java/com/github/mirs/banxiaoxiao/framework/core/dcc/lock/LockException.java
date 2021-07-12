package com.github.mirs.banxiaoxiao.framework.core.dcc.lock;

public class LockException extends RuntimeException {

	/** */
	private static final long serialVersionUID = 4248322515328966199L;

	public LockException(String msg) {
		super(msg);
	}
	
	public LockException(Exception e) {
		super(e);
	}
	
	public LockException(Throwable e) {
		super(e);
	}
	
	public LockException(Throwable e, String msg) {
		super(msg, e);
	}
}
