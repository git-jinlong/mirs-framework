/**
 * 
 */
package com.github.mirs.banxiaoxiao.framework.core.drpc.filter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.dubbo.common.Constants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.service.GenericService;
import com.github.mirs.banxiaoxiao.framework.core.error.ExceptionUtils;

/**
 * @author erxiao 2017年3月29日
 */
@Activate(group = Constants.PROVIDER, order = 1)
public class DrpcLogFilter implements Filter {

	private static final Logger logger = LoggerFactory.getLogger("TUBESERVICE");

	/**
	 * (non-Javadoc)
	 * 
	 * @see Filter#invoke(Invoker, Invocation)
	 */
	@Override
	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
		try {
			long time = System.currentTimeMillis();
			Result result = invoker.invoke(invocation);
			long now = System.currentTimeMillis();
			if (logger.isDebugEnabled()) {
				StringBuilder msg = new StringBuilder();
//				appendCommonBase(msg, invoker, invocation, now - time);
//				msg.append(" ");
				appendArguments(msg, invocation.getArguments());
				msg.append(" ");
				appendResponse(msg, result.getValue());
				msg.append(" ");
				appendException(msg, result.getException());
				if (result.hasException() && GenericService.class != invoker.getInterface()) {
					logger.error(msg.toString());
				}else {
					logger.debug(msg.toString());
				}
			} else if (result.hasException() && GenericService.class != invoker.getInterface()) {
				StringBuilder msg = new StringBuilder();
//				appendCommonBase(msg, invoker, invocation, now - time);
//				msg.append(" ");
				appendException(msg, result.getException());
				logger.error(msg.toString());
			} 
			return result;
		} catch (RuntimeException e) {
			throw e;
		}
	}

	private String auditReflectionToString(Object obj) {
		if (obj == null) {
			return "null";
		}
		return reflectionToString(obj);
	}

	private String reflectionToString(Object obj) {
		if (obj == null) {
			return "null";
		} else if (isBaseType(obj.getClass())) {
			return obj.toString();
		} else {
			return ToStringBuilder.reflectionToString(obj, OBJECT_TOSTRING_STYLE);
		}
	}

	private boolean isBaseType(Class<?> clazz) {
		return (clazz.equals(String.class) || clazz.equals(Integer.class) || clazz.equals(int.class) || clazz.equals(Byte.class)
				|| clazz.equals(byte.class) || clazz.equals(Long.class) || clazz.equals(long.class) || clazz.equals(Double.class)
				|| clazz.equals(double.class) || clazz.equals(Float.class) || clazz.equals(float.class) || clazz.equals(Character.class)
				|| clazz.equals(char.class) || clazz.equals(Short.class) || clazz.equals(short.class) || clazz.equals(BigDecimal.class)
				|| clazz.equals(Boolean.class) || clazz.equals(boolean.class) || clazz.equals(BigInteger.class) || clazz.equals(Date.class) || clazz
					.isPrimitive());
	}

	private void appendCommonBase(StringBuilder msg, Invoker<?> invoker, Invocation invocation, long time) {
	    if(RpcContext.getContext() != null) {
	        msg.append(RpcContext.getContext().getAttachment(com.github.mirs.banxiaoxiao.framework.core.config.Constants.DRPC_REQUESTID));
	        // RpcContext.getContext().isConsumerSide() 出现NEP，后续找时间深入看看
	        if (RpcContext.getContext().isConsumerSide()) {
	            msg.append(" -> ");
	        } else {
	            msg.append(" <- ");
	        }
	        msg.append(RpcContext.getContext().getRemoteHost()).append(" ");
	        msg.append(invoker.getInterface().getSimpleName()).append(".");
	        msg.append(invocation.getMethodName()).append(" (").append(time).append("ms) ").append(":");
	    }
	}

	private void appendException(StringBuilder msg, Throwable exception) {
		if (exception != null) {
			msg.append(ExceptionUtils.getStackTraceMessage(exception));
		}
	}

	private void appendResponse(StringBuilder msg, Object obj) {
	    msg.append("Response{");
        msg.append(auditReflectionToString(obj));
        msg.append("}");
	}

	private void appendArguments(StringBuilder msg, Object[] objects) {
		msg.append("Arguments[");
		int argLength = objects.length;
		for (int i = 0; i < argLength; i++) {
			msg.append(auditReflectionToString(objects[i]));
			if (i < (argLength - 1)) {
				msg.append(",");
			}
		}
		msg.append("]");
	}

	private static TubeLogStyle OBJECT_TOSTRING_STYLE = new TubeLogStyle();

	static class TubeLogStyle extends ToStringStyle {
		/**
		 * 
		 */
		private static final long serialVersionUID = -4498264599799636007L;

		public TubeLogStyle() {
			super();
			this.setUseIdentityHashCode(false);
			this.setArrayContentDetail(false);
			this.setDefaultFullDetail(true);
			this.setFieldSeparatorAtStart(true);
			this.setNullText("");
			this.setUseShortClassName(true);
			this.setContentStart("{");
			this.setContentEnd("}");

		}
	}

}
