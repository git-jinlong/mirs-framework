package com.github.mirs.banxiaoxiao.framework.common.shell;

import lombok.Data;

import java.util.List;

/**
 * @author bc
 */
@Data
public class CmdExecResult {

    private int exitCode = -1;

    private List<String> lines;
}
