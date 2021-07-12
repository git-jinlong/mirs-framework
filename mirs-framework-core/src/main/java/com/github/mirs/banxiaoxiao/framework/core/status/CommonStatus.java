package com.github.mirs.banxiaoxiao.framework.core.status;


import static com.github.mirs.banxiaoxiao.framework.core.status.DefaultStatus.toStatus;

/**
 * Define commons status.
 *
 * @author zw
 */
public final class CommonStatus {

    private CommonStatus() {
    }

    /**
     * Indicates the status is successful.
     */
    public static Status ok = toStatus(0, "success");

    /**
     * Indicates the status is successful.Alias for {@link #ok}
     */
    public static Status success = ok;

    /**
     * Indicates the status is error.
     */
    public static Status error = toStatus(10000, "error");

    /**
     * Indicates the object is not exist.
     */
    public static Status objectNotExist = toStatus(10001, "error.object_not_exist");

    /**
     * Indicates the object is exist.
     */
    public static Status objectAlreadyExist = toStatus(10002, "error.object_already_exist");

    /**
     * Indicates the argument is illegal.
     */
    public static Status illegalArgument = toStatus(10003, "error.illegal_argument");

    /**
     * Indicates currently http method is not supported.This is a alias for http 405 error code.
     */
    public static Status httpMethodNotSupport = toStatus(10405, "error.http.method.not_support");

    /**
     * Indicates the size of upload file is exceed limit.
     */
    public static Status maxUploadSizeExceeded = toStatus(10501, "error.upload.max_size_exceeded");

    /**
     * Indicates the size of upload file is exceed limit and the message support format parameters.
     */
    public static Status maxUploadSizeExceeded2 = toStatus(10501, "error.upload.max_size_exceeded2");


}
