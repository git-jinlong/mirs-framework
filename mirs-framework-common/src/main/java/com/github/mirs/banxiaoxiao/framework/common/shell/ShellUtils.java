package com.github.mirs.banxiaoxiao.framework.common.shell;

import com.github.mirs.banxiaoxiao.framework.common.util.CommandUtils;
import com.github.mirs.banxiaoxiao.framework.common.util.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Thread.NORM_PRIORITY;

/**
 * shell执行的相关工具类
 *
 * @author bc
 */
@Slf4j
public final class ShellUtils {

    private static final ThreadPoolExecutor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(5, 30, 60L, TimeUnit.SECONDS,
            new SynchronousQueue<>(), new CommandThreadFactory());

    /**
     * custom thread factory for executing commands
     */
    private static class CommandThreadFactory implements ThreadFactory {

        private final AtomicInteger count = new AtomicInteger(1);
        private final ThreadGroup group = new ThreadGroup("shell-exec-thread-group");

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(this.group, r, "shell-collect-thread-" + count.getAndIncrement());
            thread.setDaemon(true);
            thread.setPriority(NORM_PRIORITY);
            return thread;
        }
    }

    private static List<String> buildProcess(String cmd, long timeout) {
        final List<String> outputs = new ArrayList<String>();
        Process process = null;
        @SuppressWarnings("unused")
        int exitValue = 0;
        try {
            if (cmd != null && cmd.length() > 0) {
                ProcessBuilder pb = new ProcessBuilder(new String[]{"/bin/sh", "-c", cmd});
                pb.redirectErrorStream(true);
                process = pb.start();
                final InputStream input = process.getInputStream();
                THREAD_POOL_EXECUTOR.execute(new Runnable() {
                    @Override
                    public void run() {
                        Scanner scanner = new Scanner(input);
                        try {
                            while (scanner.hasNextLine()) {
                                outputs.add(scanner.nextLine());
                            }
                        } finally {
                            scanner.close();
                        }
                    }
                });
                CommandUtils.waitFor(process, (long) 5000);
                IOUtils.closeQuietly(input);
            }
        } catch (InterruptedException | IOException | TimeoutException e) {
            return null;
        } finally {
            if (process != null) {
                process.destroy();
            }
        }

        return outputs;
    }

    /**
     * 查询pid是否存在
     *
     * @param pid current pid
     * @return true=exists，false=not exists
     */
    public static boolean existsPid(int pid) {
        String cmd = "ps --no-heading " + pid + " | grep -v \"grep\" | wc -l";
        log.info("exists pid={} cmd={}", pid, cmd);

        List<String> list = buildProcess(cmd, 5000);
        log.info("exists pid={} result={}", pid, list);
        if (CollectionUtils.isEmpty(list)) {

            return false;
        }

        int value = Integer.parseInt(list.get(0));

        return value == 1;

    }

    /**
     * Query the process for pids based on keywords
     *
     * @param keyWord query keyword
     * @return If the process id exists, it returns; if it does not, it returns 0
     */
    public static int queryProcessPid(String keyWord) {

        String cmd = "ps aux | grep " + keyWord + " | grep -v \"grep\" | awk '{print $2}'";
        log.info("queryProcessId cmd=" + cmd);
        List<String> list = buildProcess(cmd, 5000);
        log.info("queryProcessId result={} ", list);

        if (CollectionUtils.isEmpty(list)) {
            log.info("queryProcessId result is null");
            return 0;
        }

        return Integer.parseInt(list.get(0));
    }

    /**
     * 执行命令
     *
     * @param cmd current cmd
     * @return result
     */
    public static List<String> exec(String cmd) {
        List<String> list = buildProcess(cmd, 5000);
        return list;
    }

    /**
     * Kill the pid process
     *
     * @param pid process's pid
     */
    public static void killPid(int pid) {

        String cmd = "kill -9 " + pid + "";

        log.info("killPid cmd=" + cmd);

        List<String> list = buildProcess(cmd, 5000);

        log.info("killPid pid={} result={}", pid, list);
    }

}
