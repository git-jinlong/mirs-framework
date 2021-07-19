package com.github.mirs.banxiaoxiao.framework.dtask.executor;

import com.github.mirs.banxiaoxiao.framework.common.util.IOUtils;
import com.github.mirs.banxiaoxiao.framework.core.log.TComLogs;
import com.github.mirs.banxiaoxiao.framework.dtask.TaskException;
import com.github.mirs.banxiaoxiao.framework.dtask.conf.TaskConfig;
import org.apache.commons.collections.CollectionUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * 外部进程任务基类，提供对进程的一些操作和检测能力
 *
 * @author zcy 2019年5月30日
 */
public abstract class ProcessTaskExecutor<T extends TaskConfig> extends BaseTaskExecutor<T> {

    public ProcessTaskExecutor() {
        this(null);
    }

    public ProcessTaskExecutor(String taskCode) {
        super(taskCode);
    }

    public ProcessTaskExecutor(T taskConfig, String taskCode) {
        super(taskConfig, taskCode);
    }

    protected void kill(String localPrimarykey) {
        String cmd = "kill -9 " + localPrimarykey + "";
        TComLogs.info("[ProcessTaskExecutor kill()] to execute kill : {}", localPrimarykey);
        buildProcess(cmd, 5000);
    }

    protected boolean isExistLocal(String localPrimarykey) {
        String cmd = "ps --no-heading " + localPrimarykey + " | grep -v \"grep\" | wc -l";
        List<String> list = buildProcess(cmd, 5000);
        if (CollectionUtils.isEmpty(list)) {
            return false;
        }
        try {
            int value = Integer.parseInt(list.get(0));
            boolean isExist = value == 1;
            if (!isExist) {
                TComLogs.debug("cmd result={},localPrimarykey={}", list.get(0), localPrimarykey);
            }
            return isExist;
        } catch (Throwable e) {
            TComLogs.warn("ps process error, ps result :", list.get(0));
            return false;
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
                Scanner scanner = new Scanner(input);
                try {
                    while (scanner.hasNextLine()) {
                        outputs.add(scanner.nextLine());
                    }
                } finally {
                    scanner.close();
                }
                IOUtils.closeQuietly(input);
            }
        } catch (Throwable e) {
            throw new TaskException("execut cmd error", e);
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return outputs;
    }

    @Override
    protected String doStart(T config) {
        int processId = startProcess(config);
        if (processId <= 0) {
            throw new TaskException("start task process fail, process id is " + processId);
        }
        return String.valueOf(processId);
    }

    protected void doStop(String localPrimarykey, T config) {
        int processId = 0;
        try {
            processId = Integer.parseInt(localPrimarykey);
        } catch (Exception e) {
        }
        stopProcess(processId, config);
    }

    /**
     * @param config
     * @return
     * @throws TaskException 有任何问题请抛异常
     */
    protected abstract int startProcess(T config) throws TaskException;

    /**
     * 停止任务，config参数可能出现为null的情况
     *
     * @param processId
     * @param config
     * @return
     * @throws TaskException 有任何问题请抛异常
     */
    protected abstract void stopProcess(int processId, T config) throws TaskException;
}
