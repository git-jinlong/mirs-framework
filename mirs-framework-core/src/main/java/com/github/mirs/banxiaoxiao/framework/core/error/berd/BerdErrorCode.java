package com.github.mirs.banxiaoxiao.framework.core.error.berd;

import com.github.mirs.banxiaoxiao.framework.core.error.ErrorCode;
import com.github.mirs.banxiaoxiao.framework.core.exception.ServiceException;
import com.github.mirs.banxiaoxiao.framework.core.status.DefaultStatus;


/**
 * 为了兼容berd老规范
 *
 * @author zcy 2020年7月2日
 */
public interface BerdErrorCode extends ErrorCode {

    public default ServiceException throwBerdException() {
        String code = getCode();
        int intCode = Integer.parseInt(code.substring(2));
        throw new ServiceException(new DefaultStatus(intCode, getMessage()));
    }

}
