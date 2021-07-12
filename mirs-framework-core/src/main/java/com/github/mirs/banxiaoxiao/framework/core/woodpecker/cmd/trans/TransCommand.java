package com.github.mirs.banxiaoxiao.framework.core.woodpecker.cmd.trans;

import com.github.mirs.banxiaoxiao.framework.common.util.IOUtils;
import com.github.mirs.banxiaoxiao.framework.common.util.StringUtil;
import com.github.mirs.banxiaoxiao.framework.core.woodpecker.config.CommandPathHolder;
import org.crsh.cli.Command;
import org.crsh.cli.Option;
import org.crsh.cli.Usage;
import org.crsh.command.BaseCommand;
import org.crsh.command.InvocationContext;
import org.springframework.util.Base64Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * @author zcy 2020年3月3日
 */
public class TransCommand extends BaseCommand {

    @SuppressWarnings("rawtypes")
    @Command
    public void main(InvocationContext context, @Usage("cmd name") @Option(names = {"c", "cmd"}) String cmd,
                     @Usage("script body") @Option(names = {"s", "script"}) String script) throws IOException {
        if (StringUtil.isBlank(cmd)) {
            throw new NullPointerException("cmd name is null");
        }
        if (StringUtil.isBlank(script)) {
            throw new NullPointerException("script is null");
        }
        String cmdDir = CommandPathHolder.getCmdDir();
        if (StringUtil.isBlank(cmdDir)) {
            throw new NullPointerException("command dir not defined");
        }
        File dirFile = new File(cmdDir);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        byte[] data = Base64Utils.decodeFromString(script);
        IOUtils.writeBytes(Paths.get(cmdDir + "/" + cmd), data);
        context.append("命令传送成功\n");
    }
}
