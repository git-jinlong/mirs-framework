package com.github.mirs.banxiaoxiao.framework.common.ssh;

import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * ssh 操作返回结果信息
 *
 * @author bc
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class SSHResInfo implements Serializable {

    /**
     * 返回状态码 （在linux中可以通过 echo $? 可知每步执行令执行的状态码）
     */
    private int exitCode;
    /**
     * 标准正确输出流内容
     */
    private List<String> outRes;
    /**
     * 错误输出
     */
    private List<String> errRes;
}
