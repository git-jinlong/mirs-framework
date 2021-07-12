package com.github.mirs.banxiaoxiao.framework.common.shell;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Thread.NORM_PRIORITY;

/**
 * @author bc
 */
@Slf4j
public class CmdLineExecutor {

  private static final ThreadPoolExecutor THREAD_POOL_EXECUTOR =
      new ThreadPoolExecutor(5, 30, 60L, TimeUnit.SECONDS,
          new SynchronousQueue<>(), new CommandThreadFactory());

  /**
   * custom thread factory for executing commands
   */
  private static class CommandThreadFactory implements ThreadFactory {

    private final AtomicInteger count = new AtomicInteger(1);
    private final ThreadGroup group = new ThreadGroup("cmd-exec-thread-group");

    @Override
    public Thread newThread(Runnable r) {
      Thread thread = new Thread(this.group, r, "cmd-collect-thread-" + count.getAndIncrement());
      thread.setDaemon(true);
      thread.setPriority(NORM_PRIORITY);
      return thread;
    }
  }

  /**
   * execute the command with defaults: default working directory waiting cmd running for 5 seconds
   */
  public static CmdExecResult execute(String[] command) {
    return execute(command, null, 5);
  }

  /**
   * execute the command with checking the exit code if equals to zero
   */
  public static CmdExecResult executeAndCheckExitCode(String[] command) {
    CmdExecResult result = execute(command);
    if (result.getExitCode() != 0) {
      log.error("Failed to execute the command {} with exit code is {}",
          Arrays.toString(command), result.getExitCode());
      throw new ShellException();
    }

    return result;
  }

  /**
   * execute the command
   */
  public static CmdExecResult execute(String[] command, File workdir, int timeout) {
    try {
      log.info("The command will be executed is {}", Arrays.toString(command));
      Process process = new ProcessBuilder(command).directory(workdir).start();
      return buildProcessResult(process, timeout);
    } catch (Exception ex) {
      log.error("Failed to execute the command {}", Arrays.toString(command));
      throw new ShellException(ex);
    }
  }

  private static CmdExecResult buildProcessResult(Process process, int timeout) {
    // collecting the command's output
    Future<List<String>> future = THREAD_POOL_EXECUTOR.submit(new OutputCollector(process));

    CmdExecResult result = new CmdExecResult();
    try {
      result.setLines(future.get(timeout, TimeUnit.SECONDS));
    } catch (Exception ex) {
      destroyQuietly(process);
      log.error("The process may timed out, part of outpout is {}", result.getLines());
      throw new ShellException(ex);
    }

    try {
      // temporarily handle fetching exit code as this way
      // but if the thread was interrupted during the waiting
      // would get the default value -1 as exit code
      // zero is a dangerous value due to would be considered
      // as the command is executed successfully
      // so caller should ensure if command taken effective in important situation
      // but actually, the thread would not be interrupted in common
      result.setExitCode(process.waitFor());
    } catch (InterruptedException ex) {
      log.error("The thread was interrupted during waiting for subprocess exit code");
      Thread.currentThread().interrupt();
    }

    return result;
  }

  /**
   * destroy the process quietly
   */
  @SuppressWarnings("squid:S1872")
  private static void destroyQuietly(Process process) {
    if (process.isAlive()) {
      process.destroy();

      // only worked in unix-like systems, the class not exists in windows platform
      Class<? extends Process> clazz = process.getClass();
      Long pid = -1L;
      try {
        if (clazz.getName().equals("java.lang.UNIXProcess")) {
          Field f = clazz.getDeclaredField("pid");
          f.setAccessible(true);
          pid = f.getLong(process);
        }
      } catch (Exception ex) {
        // do nothing
      }

      log.error("Destroy the process with id {}", pid);
    }
  }

  private static class OutputCollector implements Callable<List<String>> {

    private Process process;
    private List<String> container;

    OutputCollector(Process process) {
      this.process = process;
      this.container = new ArrayList<>();
    }

    @Override
    public List<String> call() {
      try (InputStream istream = process.getInputStream();
          BufferedReader reader = new BufferedReader(new InputStreamReader(istream))) {
        // reader#readline is an blocking operation !!!
        // so it's not so neccesary to set a flag to stop the thread
        // if the command's process exit, whether in normal or be killed
        // the readline operation would return by itself
        String line;

        while ((line = reader.readLine()) != null) {
          log.debug("Read the process output {}", line);
          this.container.add(line);
        }
      } catch (Exception ex) {
        log.error("Error while reading the process output", ex);
      }

      return this.container;
    }
  }

  private CmdLineExecutor() {
  }
}
