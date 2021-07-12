package com.github.mirs.banxiaoxiao.framework.web;


import com.github.mirs.banxiaoxiao.framework.core.exception.ServiceException;
import com.github.mirs.banxiaoxiao.framework.core.status.CommonStatus;
import com.github.mirs.banxiaoxiao.framework.web.rest.RestResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.stream.Collectors;

import static com.github.mirs.banxiaoxiao.framework.core.status.DefaultStatus.toStatus;

/**
 * Abstract base class for controller that defines some commonly functionality.
 *
 * @author zw
 */
public abstract class ControllerSupport {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private MessageSource messageSource;

    @Autowired
    protected RestResponses restResponses;

    /**
     * Checks and throw {@code ServiceException} if the result is invalid.
     *
     * @param result the instance of {@link BindingResult}
     * @throws ServiceException if the result is invalid
     */
    protected void checkBindingResult(BindingResult result) {
        if (result.hasErrors()) {
            String errorMsg = result.getAllErrors().stream().map(this::translateError)
                    .collect(Collectors.joining(","));
            throw new ServiceException(toStatus(CommonStatus.illegalArgument.getCode(), errorMsg));
        }
    }

    /**
     * Translate the error to message.
     *
     * @param error the error instance
     * @return the message corresponding to error
     */
    private String translateError(ObjectError error) {
        String prefix = "";
        if (error instanceof FieldError) {
            FieldError fieldError = (FieldError) error;
            prefix = fieldError.getField();
        }
        return "[" + prefix + messageSource.getMessage(error, null) + "]";
    }


}
