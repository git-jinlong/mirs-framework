package com.github.mirs.banxiaoxiao.framework.core.dcc.lock;

public class LockId implements Comparable<LockId> {
	
	private String name;
	
	public LockId(String id) {
		this.name = id;
	}
	
	public String getName() {
		return this.name;
	}

	@Override
	public int compareTo(LockId arg0) {
		return this.name.compareTo(arg0.name);
	}
}
