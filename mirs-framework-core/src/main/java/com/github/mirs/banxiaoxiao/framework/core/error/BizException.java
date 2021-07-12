/**
 * 
 */
package com.github.mirs.banxiaoxiao.framework.core.error;

/**
 * @author erxiao
 *
 */
public class BizException extends RuntimeException {

    /**
	 * 
	 */
    private static final long serialVersionUID = 6343717153965726321L;

    private ErrorCode error;

    public BizException() {
        super();
    }

    public BizException(ErrorCode error) {
        super(error.getCode() + "(" + error.getMessage()+")");
        this.error = error;
    }

    public BizException(ErrorCode error, String message) {
        super(error.getCode() + "(" + error.getMessage()+") : " + message);
        this.error = error;
    }

    public BizException(ErrorCode error, Throwable exception) {
        super(error.getCode() + "(" + error.getMessage()+")" +" " + exception.getMessage(), exception);
        this.error = error;
    }
    
    public BizException(ErrorCode error, String message, Throwable exception) {
        super(error.getCode() + "(" + error.getMessage()+") : " + message + " " + exception.getMessage(), exception);
        this.error = error;
    }
    
    /**
     * @return the errorContext
     */
    public ErrorCode getError() {
        return this.error;
    }

}
