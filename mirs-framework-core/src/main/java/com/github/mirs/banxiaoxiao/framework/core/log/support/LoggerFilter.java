/**
 * 
 */
package com.github.mirs.banxiaoxiao.framework.core.log.support;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.AbstractMatcherFilter;
import ch.qos.logback.core.spi.FilterReply;

/**
 * @author erxiao 2017年4月10日
 */
public class LoggerFilter extends AbstractMatcherFilter<ILoggingEvent> {

	private String logger;

	/**
	 * (non-Javadoc)
	 * 
	 * @see ch.qos.logback.core.filter.Filter#decide(Object)
	 */
	@Override
	public FilterReply decide(ILoggingEvent event) {
		if (!isStarted()) {
			return FilterReply.NEUTRAL;
		}

		if (event.getLoggerName().equals(logger)) {
			return onMatch;
		} else {
			return onMismatch;
		}
	}

	public void setLogger(String logger) {
		this.logger = logger;
	}

	public void start() {
		if (this.logger != null) {
			super.start();
		}
	}

}
