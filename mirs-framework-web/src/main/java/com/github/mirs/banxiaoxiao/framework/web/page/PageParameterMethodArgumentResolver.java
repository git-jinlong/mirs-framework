package com.github.mirs.banxiaoxiao.framework.web.page;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.core.MethodParameter;
import org.springframework.util.Assert;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * A {@code PageParameterMethodArgumentResolver} for paging.
 *
 * @author zw
 * @see Page
 * @see PageParameter
 */
public class PageParameterMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String DEFAULT_PAGE_NUMBER_PARAMETER = "pageNumber";
    private static final String DEFAULT_PAGE_SIZE_PARAMETER = "pageSize";
    private static final String DEFAULT_ORDER_BY_PARAMETER = "orderBy";
    private static final String DEFAULT_ORDER_PARAMETER = "order";

    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int DEFAULT_PAGE_NUMBER = 1;
    private static final PageParameter.Order DEFAULT_ORDER = PageParameter.Order.desc;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return PageParameter.class.equals(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Page annotation = parameter.getMethodAnnotation(Page.class);
        Assert.notNull(annotation, "Couldn't find @Page annotation in method " + parameter.getMethod());
        String pageNumberStr = webRequest.getParameter(DEFAULT_PAGE_NUMBER_PARAMETER);
        int pageNumber = StringUtils.isNotBlank(pageNumberStr)
                ? parseAndApplyBoundaries(pageNumberStr, DEFAULT_PAGE_NUMBER, Integer.MAX_VALUE)
                : DEFAULT_PAGE_NUMBER;

        String pageSizeStr = webRequest.getParameter(DEFAULT_PAGE_SIZE_PARAMETER);
        int pageSize = StringUtils.isNotBlank(pageSizeStr)
                ? parseAndApplyBoundaries(pageSizeStr, DEFAULT_PAGE_SIZE, Integer.MAX_VALUE)
                : DEFAULT_PAGE_SIZE;

        String orderByField = webRequest.getParameter(DEFAULT_ORDER_BY_PARAMETER);
        PageParameter.Order order = DEFAULT_ORDER;
        if (StringUtils.isNotBlank(orderByField)) {
            PageParameter.Order tempOrder = EnumUtils
                    .getEnum(PageParameter.Order.class, webRequest.getParameter(DEFAULT_ORDER_PARAMETER));
            if (tempOrder != null) {
                order = tempOrder;
            }
        }
        return new PageParameter(pageNumber, pageSize,
                validateAndRetrieveOrderByField(annotation, orderByField), order);
    }

    private String validateAndRetrieveOrderByField(Page annotation, String orderBy) {
        String[] orderBys = annotation.allowOrderByFields();
        if (ArrayUtils.indexOf(orderBys, orderBy) == -1) {
            return StringUtils.isBlank(annotation.orderBy()) ? orderBys[0] : annotation.orderBy();
        }
        return orderBy;
    }


    private static int parseAndApplyBoundaries(String str, int lower, int upper) {
        int value = NumberUtils.toInt(str, lower);
        return value <= 0 ? lower : (value < lower ? value : value > upper ? upper : value);
    }
}
