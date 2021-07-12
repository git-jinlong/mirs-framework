/**
 * 
 */
package com.github.mirs.banxiaoxiao.framework.core.error;

import java.util.Collection;
import java.util.Map;

/**
 * 断言类，如果不使用自定义异常类型，默认抛出参数异常错误：{@link CommonErrorCode#ILLEGAL_ARGUMENT_EXCEPTION}
 * 
 * @author erxiao 2016年1月27日
 */
public class BizAssert {

	protected BizAssert() {
	}

	/**
	 * 校验是否 number > 0，不成立则抛出异常：{@link CommonErrorCode#ILLEGAL_ARGUMENT_EXCEPTION}
	 * 
	 * @param number
	 */
	public static <T extends Number> void greaterZero(T number) {
		greaterZero(number, "[Assertion failed] - this number must be greater than zero");
	}

	/**
	 * 校验是否 number > 0，不成立则抛出异常：{@link CommonErrorCode#ILLEGAL_ARGUMENT_EXCEPTION}
	 * 
	 * @param number
	 * @param errorMessage
	 */
	public static <T extends Number> void greaterZero(T number, String errorMessage, Object... args) {
		greaterZero(number, CommonErrorCode.ILLEGAL_ARGUMENT_EXCEPTION, String.format(errorMessage, args));
	}

	/**
	 * 校验是否 number > 0， 建议使用：{@linkplain BizAssert#greaterZero(Number, BaseError, String)}
	 * 
	 * @param number
	 * @param errorCode
	 * @param errorMessage
	 */
	@Deprecated
	public static <T extends Number> void greaterZero(T number, ErrorCode errorCode, String errorMessage, Object... args) {
		if (number == null || number.doubleValue() <= 0) {
			throwBusinessException(errorCode, String.format(errorMessage, args));
		}
	}

	/**
	 * 校验是否 num1 > num2，不成立则抛出异常：{@link CommonErrorCode#ILLEGAL_ARGUMENT_EXCEPTION}
	 * 
	 * @param num1
	 * @param num2
	 * @param errorMessage
	 */
	public static <T extends Number> void greaterNum(T num1, T num2, String errorMessage, Object... args) {
		greaterNum(num1, num2, CommonErrorCode.ILLEGAL_ARGUMENT_EXCEPTION, String.format(errorMessage, args));
	}

	/**
	 * 校验是否 num1 > num2，建议使用：{@linkplain BizAssert#greaterNum(Number, Number, BaseError, String) }
	 * 
	 * @param number
	 * @param num2
	 * @param errorMessage
	 */
	@Deprecated
	public static <T extends Number> void greaterNum(T num1, T num2, ErrorCode errorCode, String errorMessage, Object... args) {
		if (num1 == null || num2 == null || num1.doubleValue() <= num2.doubleValue()) {
			throwBusinessException(errorCode, String.format(errorMessage, args));
		}
	}

	/**
	 * 校验是否 num1 >= num2，不成立则抛出异常：{@link CommonErrorCode#ILLEGAL_ARGUMENT_EXCEPTION}
	 * 
	 * @param number
	 * @param num2
	 * @param errorMessage
	 */
	public static <T extends Number> void greaterAndEquNum(T num1, T num2, String errorMessage, Object... args) {
		greaterAndEquNum(num1, num2, CommonErrorCode.ILLEGAL_ARGUMENT_EXCEPTION, String.format(errorMessage, args));
	}

	/**
	 * 校验是否 num1 >= num2，建议使用：{@link BizAssert#greaterAndEquNum(Number, Number, BaseError, String) }
	 * 
	 * @param number
	 * @param num2
	 * @param errorCode
	 * @param errorMessage
	 */
	@Deprecated
	public static <T extends Number> void greaterAndEquNum(T num1, T num2, ErrorCode errorCode, String errorMessage, Object... args) {
		if (num1 == null || num2 == null || num1.doubleValue() < num2.doubleValue()) {
			throwBusinessException(errorCode, String.format(errorMessage, args));
		}
	}

	/**
	 * 校验是否 相关对象是否相等,如果两个对象都为null，认为相等，不成立则抛出异常：{@link CommonErrorCode#ILLEGAL_ARGUMENT_EXCEPTION}
	 * 
	 * @param number
	 * @param num2
	 * @param errorMessage
	 */
	public static void equals(Object obj1, Object obj2, String errorMessage, Object... args) {
		equals(obj1, obj2, CommonErrorCode.ILLEGAL_ARGUMENT_EXCEPTION, String.format(errorMessage, args));
	}

	/**
	 * 校验是否 相关对象是否相等,如果两个对象都为null，认为相等，建议使用：{@linkplain BizAssert#equals(Object, Object, BaseError, String) }
	 * 
	 * @param number
	 * @param num2
	 * @param errorCode
	 * @param errorMessage
	 */
	@Deprecated
	public static void equals(Object obj1, Object obj2, ErrorCode errorCode, String errorMessage, Object... args) {
		if (obj1 == null) {
			if (obj2 != null) {
				throwBusinessException(errorCode, String.format(errorMessage, args));
			}
		} else {
			if (!obj1.equals(obj2)) {
				throwBusinessException(errorCode, String.format(errorMessage, args));
			}
		}
	}

	/**
	 * 如果断言为假，则抛出错误码为：{@link CommonErrorCode#ILLEGAL_ARGUMENT_EXCEPTION}的异常
	 * 
	 * @param expression
	 */
	public static void isTrue(boolean expression) {
		isTrue(expression, "[Assertion failed] - this expression must be true");
	}

	/**
	 * 如果断言为假，则抛出错误码为：{@link CommonErrorCode#ILLEGAL_ARGUMENT_EXCEPTION}的异常
	 * 
	 * @param expression
	 */
	public static void isTrue(boolean expression, String errorMessage, Object... args) {
		isTrue(expression, CommonErrorCode.ILLEGAL_ARGUMENT_EXCEPTION, String.format(errorMessage, args));
	}

	/**
	 * 判断目标对象断言为假，则抛出目标错误码和错误信息的异常。建议使用{@link #isTrue(boolean, BaseError, String)}
	 * 
	 * @param expression
	 * @param errorCode
	 * @param errorMessage
	 */
	@Deprecated
	public static void isTrue(boolean expression, ErrorCode errorCode, String errorMessage, Object... args) {
		if (!expression) {
			throwBusinessException(errorCode, String.format(errorMessage, args));
		}
	}

	public static void notBlank(String expression) {
		notBlank(expression, "[Assertion failed] - this String argument must have length; it must not be null or empty or blank");
	}

	public static void notBlank(String expression, String errorMessage, Object... args) {
		notBlank(expression, CommonErrorCode.ILLEGAL_ARGUMENT_EXCEPTION, String.format(errorMessage, args));
	}

	/**
	 * 建议使用：{@linkplain BizAssert#notBlank(String, BaseError, String) }
	 * 
	 * @param expression
	 * @param errorCode
	 * @param errorMessage
	 * @param args
	 */
	@Deprecated
	public static void notBlank(String expression, ErrorCode errorCode, String errorMessage, Object... args) {
		if (expression == null || expression.trim().length() == 0) {
			throwBusinessException(errorCode, String.format(errorMessage, args));
		}
	}

	/**
	 * 判断目标对象是否不为空，如果为空，则抛出异常：{@link CommonErrorCode#ILLEGAL_ARGUMENT_EXCEPTION}
	 * 
	 * @param obj
	 */
	public static void notNull(Object obj) {
		notNull(obj, "[Assertion failed] - this expression must be not null");
	}

	/**
	 * 判断目标对象是否不为空，如果为空，则抛出异常：{@link CommonErrorCode#ILLEGAL_ARGUMENT_EXCEPTION}
	 * 
	 * @param obj
	 */
	public static void notNull(Object obj, String errorMessage, Object... args) {
		notNull(obj, CommonErrorCode.ILLEGAL_ARGUMENT_EXCEPTION, String.format(errorMessage, args));
	}

	/**
	 * 判断目标对象是否不为空，如果为空，则抛出目标错误码和错误信息的异常，建议使用：{@linkplain BizAssert#notNull(Object, BaseError, String) }
	 * 
	 * @param obj
	 * @param errorCode
	 * @param errorMessage
	 */
	@Deprecated
	public static void notNull(Object obj, ErrorCode errorCode, String errorMessage, Object... args) {
		if (obj == null) {
			throwBusinessException(errorCode, String.format(errorMessage, args));
		}
	}

	public static void isNull(Object obj) {
		isNull(obj, CommonErrorCode.ILLEGAL_ARGUMENT_EXCEPTION, "[Assertion failed] - this expression must be is null");
	}

	public static void notEmpty(Object[] array) {
		if (verifyEmpty(array)) {
			throwCommonException("[Assertion failed] - this array must not be empty: it must contain at least 1 element");
		}
	}

	/**
	 * 判断目标对象是否不为空，如果为空，则抛出目标错误码和错误信息的异常，建议使用：{@linkplain BizAssert#isNull(Object, BaseError, String) }
	 * 
	 * @param obj
	 * @param errorCode
	 * @param errorMessage
	 */
	@Deprecated
	public static void isNull(Object obj, ErrorCode errorCode, String errorMessage, Object... args) {
		if (obj != null) {
			throwBusinessException(errorCode, String.format(errorMessage, args));
		}
	}

	/**
	 * 建议使用：{@linkplain BizAssert#notEmpty(Object[], BaseError, String) }
	 * @param array
	 * @param errorCode
	 * @param errorMessage
	 * @param args
	 */
	@Deprecated
	public static void notEmpty(Object[] array, ErrorCode errorCode, String errorMessage, Object... args) {
		if (verifyEmpty(array)) {
			throwBusinessException(errorCode, String.format(errorMessage, args));
		}
	}

	public static void notEmpty(Collection<?> collection) {
		if (verifyEmpty(collection)) {
			throwCommonException("[Assertion failed] - this collection must not be empty: it must contain at least 1 element");
		}
	}

	/**
	 * 建议使用：{@linkplain BizAssert#notEmpty(Collection, BaseError, String) }
	 * @param collection
	 * @param errorCode
	 * @param errorMessage
	 * @param args
	 */
	@Deprecated
	public static void notEmpty(Collection<?> collection, ErrorCode errorCode, String errorMessage, Object... args) {
		if (verifyEmpty(collection)) {
			throwBusinessException(errorCode, String.format(errorMessage, args));
		}
	}

	public static void notEmpty(Map<?, ?> map) {
		if (verifyEmpty(map)) {
			throwCommonException("[Assertion failed] - this map must not be empty; it must contain at least one entry");
		}
	}

	/**
	 * 建议使用：{@linkplain BizAssert#notEmpty(Map, BaseError, String) }
	 * @param map
	 * @param errorCode
	 * @param errorMessage
	 * @param args
	 */
	@Deprecated
	public static void notEmpty(Map<?, ?> map, ErrorCode errorCode, String errorMessage, Object... args) {
		if (verifyEmpty(map)) {
			throwBusinessException(errorCode, String.format(errorMessage, args));
		}
	}

	/**
	 * 校验是否 long1 和 long2 相差是否小于或等于difference, 如果大于抛出异常，建议使用：{@linkplain BizAssert#differenceNotMore(Long, Long, Long, BaseError, String) }
	 * 
	 * @param number
	 * @param num2
	 * @param errorMessage
	 */
	@Deprecated
	public static void differenceNotMore(Long long1, Long long2, Long difference, ErrorCode errorCode, String errorMessage) {
		if (Math.abs(long1 - long2) > difference) {
			throwBusinessException(errorCode, errorMessage);
		}
	}

	// 以下增加基于BaseEror的断言，方便直接从BaseEror中获取错误描述

	/**
	 * 如果为假，则抛出BizException异常，异常描述信息为：error.getMessage() + message
	 * 
	 * @param expression
	 * @param error
	 * @param message
	 */
	public static void isTrue(boolean expression, ErrorCode error, String message) {
		if (!expression) {
			ExceptionUtils.throwException(error, message);
		}
	}

	/**
	 * 校验是否 number > 0，异常描述信息为：error.getMessage() + message
	 * 
	 * @param number
	 * @param error
	 * @param message
	 */
	public static <T extends Number> void greaterZero(T number, ErrorCode error, String message) {
		if (number == null || number.doubleValue() <= 0) {
			ExceptionUtils.throwException(error, message);
		}
	}

	/**
	 * 校验是否 num1 > num2，异常描述信息为：error.getMessage() + message
	 * 
	 * @param number
	 * @param num2
	 * @param error
	 * @param message
	 */
	public static <T extends Number> void greaterNum(T num1, T num2, ErrorCode error, String message) {
		if (num1 == null || num2 == null || num1.doubleValue() <= num2.doubleValue()) {
			ExceptionUtils.throwException(error, message);
		}
	}

	/**
	 * 校验是否 num1 >= num2，异常描述信息为：error.getMessage() + message
	 * 
	 * @param number
	 * @param num2
	 * @param error
	 * @param message
	 */
	public static <T extends Number> void greaterAndEquNum(T num1, T num2, ErrorCode error, String message) {
		if (num1 == null || num2 == null || num1.doubleValue() < num2.doubleValue()) {
			ExceptionUtils.throwException(error, message);
		}
	}

	/**
	 * 断言是否 相关对象是否相等,如果两个对象都为null，认为相等，异常描述信息为：error.getMessage() + message
	 * 
	 * @param number
	 * @param num2
	 * @param error
	 * @param message
	 */
	public static void equals(Object obj1, Object obj2, ErrorCode error, String message) {
		if (obj1 == null) {
			if (obj2 != null) {
				ExceptionUtils.throwException(error, message);
			}
		} else {
			if (!obj1.equals(obj2)) {
				ExceptionUtils.throwException(error, message);
			}
		}
	}

	/**
	 * 断言字符串不为空blank，异常描述信息为：error.getMessage() + message
	 * 
	 * @param str
	 * @param error
	 * @param message
	 */
	public static void notBlank(String str, ErrorCode error, String message) {
		if (str == null || str.trim().length() == 0) {
			ExceptionUtils.throwException(error, message);
		}
	}

	/**
	 * 判断目标对象是否不为空，如果为空，则抛出目标错误码和错误信息的异常，异常描述信息为：error.getMessage() + message
	 * 
	 * @param obj
	 * @param error
	 * @param message
	 */
	public static void notNull(Object obj, ErrorCode error, String message) {
		if (obj == null) {
			ExceptionUtils.throwException(error, message);
		}
	}

	/**
	 * 判断目标对象是否不为空，如果为空，则抛出目标错误码和错误信息的异常，异常描述信息为：error.getMessage() + message
	 * 
	 * @param obj
	 * @param error
	 * @param message
	 */
	public static void isNull(Object obj, ErrorCode error, String message) {
		if (obj != null) {
			ExceptionUtils.throwException(error, message);
		}
	}

	/**
	 * 异常描述信息为：error.getMessage() + message
	 * 
	 * @param array
	 * @param error
	 * @param message
	 */
	public static void notEmpty(Object[] array, ErrorCode error, String message) {
		if (verifyEmpty(array)) {
			ExceptionUtils.throwException(error, message);
		}
	}

	/**
	 * 异常描述信息为：error.getMessage() + message
	 * 
	 * @param collection
	 * @param error
	 * @param message
	 */
	public static void notEmpty(Collection<?> collection, ErrorCode error, String message) {
		if (verifyEmpty(collection)) {
			ExceptionUtils.throwException(error, message);
		}
	}

	/**
	 * 异常描述信息为：error.getMessage() + message
	 * 
	 * @param map
	 * @param error
	 * @param message
	 */
	public static void notEmpty(Map<?, ?> map, ErrorCode error, String message) {
		if (verifyEmpty(map)) {
			ExceptionUtils.throwException(error, message);
		}
	}

	protected static boolean verifyEmpty(Object[] array) {
		return array == null || array.length == 0;
	}

	protected static boolean verifyEmpty(Collection<?> collection) {
		return collection == null || collection.size() == 0;
	}

	protected static boolean verifyEmpty(Map<?, ?> map) {
		return map == null || map.size() == 0;
	}

	protected static void throwCommonException(String errorMessage) {
		throw new BizException(CommonErrorCode.ILLEGAL_ARGUMENT_EXCEPTION, errorMessage);
	}

	/**
	 * 当断言业务错误为假时抛出异常，默认抛出 {@link com.BizException.sesame.common.exception.SesameRuntimeException}<br>
	 * 
	 * @param errorCode
	 * @param errorMessage
	 */
	protected static void throwBusinessException(ErrorCode errorCode, String errorMessage) {
		throw new BizException(errorCode, errorMessage);
	}
}
