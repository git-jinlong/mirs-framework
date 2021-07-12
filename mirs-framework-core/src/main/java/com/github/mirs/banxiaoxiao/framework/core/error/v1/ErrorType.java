package com.github.mirs.banxiaoxiao.framework.core.error.v1;

/**
 * 标准错误码类型，根据实际捕获异常的场景选择类型
 * 
 * @author erxiao
 *
 */
public enum ErrorType {

	/** 业务服务处理异常类型，如：必须的业务请求参数不正确，调用下游服务出现业务异常等 */
	SERVICE,

	/** 系统异常类型，如：jvm异常、网络连接异常、IO异常等不可预知异常 */
	SYSTEM;

}
