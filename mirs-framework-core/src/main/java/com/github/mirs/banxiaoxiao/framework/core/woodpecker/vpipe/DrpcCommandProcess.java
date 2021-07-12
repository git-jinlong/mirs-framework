package com.github.mirs.banxiaoxiao.framework.core.woodpecker.vpipe;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.crsh.cli.impl.Delimiter;
import org.crsh.cli.impl.completion.CompletionMatch;
import org.crsh.cli.spi.Completion;
import org.crsh.plugin.PluginLifeCycle;
import org.crsh.shell.Shell;
import org.crsh.shell.ShellFactory;
import org.crsh.shell.ShellProcess;
import org.crsh.shell.impl.command.CRaSHShellFactory;
import org.crsh.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.mirs.banxiaoxiao.framework.core.log.TComLogs;
import com.github.mirs.banxiaoxiao.framework.core.util.DataTimeWindow;

/**
 * @author zcy 2019年6月4日
 */
public class DrpcCommandProcess implements CommandProcess {

    private DataTimeWindow<DrpcSession> sessions = new DataTimeWindow<DrpcSession>(TimeUnit.SECONDS, 120, "DrpcCommandProcess");

    @Autowired
    private PluginLifeCycle cycle;

    @Override
    public List<String> invoke(Command command) {
        TComLogs.debug("invoke command {}", command.getCommand());
        Principal principal = command.getPrincipal();
        DrpcSession query = new DrpcSession(principal);
        DrpcSession session = sessions.pick(query);
        if (session == null) {
            synchronized (this) {
                session = sessions.pick(query);
                if (session == null) {
                    session = new DrpcSession(principal);
                    session.setWidth(command.getWidth());
                    session.setHeight(command.getHeight());
                    ShellFactory factory = cycle.getContext().getPlugin(ShellFactory.class);
                    Shell shell = ((CRaSHShellFactory) factory).create(principal, false);
                    session.setShell(shell);
                    sessions.push(session);
                }
            }
        }
        try {
            String cmd = command.getCommand().trim();
            if (cmd.equals("hi")) {
                return Arrays.asList("hi", "\n");
            } else if (cmd.equals("bye")) {
                ShellProcess process = session.getShell().createProcess(cmd);
                process.cancel();
                session.getShell().close();
                sessions.pick(session);
            } else if (cmd.startsWith("association")) {
                String prefix = cmd.substring("association".length()).trim();
                List<String> ass = association(session, prefix);
                session.setResult(ass);
            } else {
                ShellProcess process = session.getShell().createProcess(cmd);
                process.execute(session);
            }
        } catch (IOException e) {
            TComLogs.error("", e);
        } finally {
            session.refresh();
            session.flush();
        }
        return session.getResultAndClean();
    }

    private List<String> association(DrpcSession session, String prefix) {
        CompletionMatch completion = session.getShell().complete(prefix);
        Completion completions = completion.getValue();
        Delimiter delimiter = completion.getDelimiter();
        StringBuilder sb = new StringBuilder();
        List<String> values = new ArrayList<String>();
        try {
            if (completions.getSize() == 1) {
                String value = completions.getValues().iterator().next();
                delimiter.escape(value, sb);
                if (completions.get(value)) {
                    sb.append(delimiter.getValue());
                }
                values.add(sb.toString());
            } else {
                String commonCompletion = Utils.findLongestCommonPrefix(completions.getValues());
                if (commonCompletion.length() > 0) {
                    delimiter.escape(commonCompletion, sb);
                    values.add(sb.toString());
                } else {
                    for (Map.Entry<String, Boolean> entry : completions) {
                        delimiter.escape(entry.getKey(), sb);
                        values.add(sb.toString());
                        sb.setLength(0);
                    }
                }
            }
        } catch (IOException ignore) {
            // Should not happen
        }
        return values;
    }
}
