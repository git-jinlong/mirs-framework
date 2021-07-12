package com.github.mirs.banxiaoxiao.framework.common.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

/**
 * Command line execute utils.
 * 
 * @author fjli
 * @since 1.0.1
 */
public class CommandUtils {

	private static final String DEFAULT_ENCODING = "UTF-8";

	private CommandUtils() {
	}

	/**
	 * Execute the specified shell command without timeout. The process exit
	 * value must be 0, and the empty lines will be skipped.
	 * 
	 * @param cmd - the command to execute
	 * @return the output strings.
	 * @throws IOException if execute failed.
	 * @throws InterruptedException if the current thread was interrupted while waiting.
	 * @throws SecurityException - If a security manager exists and its checkExec method doesn't allow creation of the subprocess.
	 * @since 1.0.3
	 */
	public static List<String> executeShell(String cmd) throws IOException, InterruptedException {
		return executeShell(cmd, DEFAULT_ENCODING);
	}

	/**
	 * Execute the specified shell command without timeout. The process exit
	 * value must be 0, and the empty lines will be skipped.
	 * 
	 * @param cmd - the command to execute
	 * @param charset - the charset to encoding
	 * @return the output strings.
	 * @throws IOException if execute failed.
	 * @throws InterruptedException if the current thread was interrupted while waiting.
	 * @throws SecurityException - If a security manager exists and its checkExec method doesn't allow creation of the subprocess.
	 * @since 1.0.6
	 */
	public static List<String> executeShell(String cmd, String charset) throws IOException, InterruptedException {
		return executeShell(cmd, charset, false, true);
	}

	/**
	 * Execute the specified shell command without timeout.
	 * 
	 * @param cmd - the command to execute
	 * @param charset - the charset to encoding
	 * @param ignoreExitValue - ignore exit value
	 * @param skipEmptyLines - skip empty lines
	 * @return the output strings.
	 * @throws IOException if execute failed.
	 * @throws InterruptedException if the current thread was interrupted while waiting.
	 * @throws SecurityException - If a security manager exists and its checkExec method doesn't allow creation of the subprocess.
	 * @since 1.0.6
	 */
	public static List<String> executeShell(String cmd, String charset, boolean ignoreExitValue, boolean skipEmptyLines) throws IOException, InterruptedException {
		return executeShell(new String[] { cmd }, charset, ignoreExitValue, skipEmptyLines);
	}

	/**
	 * Execute the specified shell command within timeout. The process exit
	 * value must be 0, and the empty lines will be skipped.
	 * 
	 * @param cmd - the command to execute
	 * @param timeout - the execute timeout
	 * @return the output strings.
	 * @throws IOException if execute failed.
	 * @throws InterruptedException if the current thread was interrupted while waiting.
	 * @throws TimeoutException if the wait timed out.
	 * @throws SecurityException - If a security manager exists and its checkExec method doesn't allow creation of the subprocess.
	 * @since 1.0.3
	 */
	public static List<String> executeShell(String cmd, Long timeout) throws IOException, InterruptedException, TimeoutException {
		return executeShell(cmd, DEFAULT_ENCODING, timeout);
	}

	/**
	 * Execute the specified shell command within timeout. The process exit
	 * value must be 0, and the empty lines will be skipped.
	 * 
	 * @param cmd - the command to execute
	 * @param charset - the charset to encoding
	 * @param timeout - the execute timeout
	 * @return the output strings.
	 * @throws IOException if execute failed.
	 * @throws InterruptedException if the current thread was interrupted while waiting.
	 * @throws TimeoutException if the wait timed out.
	 * @throws SecurityException - If a security manager exists and its checkExec method doesn't allow creation of the subprocess.
	 * @since 1.0.6
	 */
	public static List<String> executeShell(String cmd, String charset, Long timeout) throws IOException, InterruptedException, TimeoutException {
		return executeShell(cmd, charset, timeout, false, true);
	}

	/**
	 * Execute the specified shell command within timeout.
	 * 
	 * @param cmd - the command to execute
	 * @param charset - the charset to encoding
	 * @param timeout - the execute timeout
	 * @param ignoreExitValue - ignore exit value
	 * @param skipEmptyLines - skip empty lines
	 * @return the output strings.
	 * @throws IOException if execute failed.
	 * @throws InterruptedException if the current thread was interrupted while waiting.
	 * @throws TimeoutException if the wait timed out.
	 * @throws SecurityException - If a security manager exists and its checkExec method doesn't allow creation of the subprocess.
	 * @since 1.0.6
	 */
	public static List<String> executeShell(String cmd, String charset, Long timeout, boolean ignoreExitValue, boolean skipEmptyLines) throws IOException, InterruptedException, TimeoutException {
		return executeShell(new String[] { cmd }, charset, timeout, ignoreExitValue, skipEmptyLines);
	}

	/**
	 * Execute the specified shell command within timeout. The process exit
	 * value must be 0, and the empty lines will be skipped.
	 * 
	 * @param cmds - the command to execute
	 * @return the output strings.
	 * @throws IOException if execute failed.
	 * @throws InterruptedException if the current thread was interrupted while waiting.
	 * @throws SecurityException - If a security manager exists and its checkExec method doesn't allow creation of the subprocess.
	 * @since 1.0.3
	 */
	public static List<String> executeShell(String... cmds) throws IOException, InterruptedException {
		return executeShell(cmds, DEFAULT_ENCODING);
	}

	/**
	 * Execute the specified shell command within timeout. The process exit
	 * value must be 0, and the empty lines will be skipped.
	 * 
	 * @param cmds - the command to execute
	 * @param charset - the charset to encoding
	 * @return the output strings.
	 * @throws IOException if execute failed.
	 * @throws InterruptedException if the current thread was interrupted while waiting.
	 * @throws SecurityException - If a security manager exists and its checkExec method doesn't allow creation of the subprocess.
	 * @since 1.0.6
	 */
	public static List<String> executeShell(String[] cmds, String charset) throws IOException, InterruptedException {
		return executeShell(cmds, charset, false, true);
	}

	/**
	 * Execute the specified shell command within timeout.
	 * 
	 * @param cmds - the command to execute
	 * @param charset - the charset to encoding
	 * @param ignoreExitValue - ignore exit value
	 * @param skipEmptyLines - skip empty lines
	 * @return the output strings.
	 * @throws IOException if execute failed.
	 * @throws InterruptedException if the current thread was interrupted while waiting.
	 * @throws SecurityException - If a security manager exists and its checkExec method doesn't allow creation of the subprocess.
	 * @since 1.0.6
	 */
	public static List<String> executeShell(String[] cmds, String charset, boolean ignoreExitValue, boolean skipEmptyLines) throws IOException, InterruptedException {
		return executeCommand(getShellCommand(cmds), charset, ignoreExitValue, skipEmptyLines);
	}

	/**
	 * Execute the specified shell command within timeout. The process exit
	 * value must be 0, and the empty lines will be skipped.
	 * 
	 * @param cmds - the command to execute
	 * @param timeout - the execute timeout
	 * @return the output strings.
	 * @throws IOException if execute failed.
	 * @throws InterruptedException if the current thread was interrupted while waiting.
	 * @throws TimeoutException if the wait timed out.
	 * @throws SecurityException - If a security manager exists and its checkExec method doesn't allow creation of the subprocess.
	 * @since 1.0.3
	 */
	public static List<String> executeShell(String[] cmds, Long timeout) throws IOException, InterruptedException, TimeoutException {
		return executeShell(cmds, DEFAULT_ENCODING, timeout);
	}

	/**
	 * Execute the specified shell command within timeout. The process exit
	 * value must be 0, and the empty lines will be skipped.
	 * 
	 * @param cmds - the command to execute
	 * @param charset - the charset to encoding
	 * @param timeout - the execute timeout
	 * @return the output strings.
	 * @throws IOException if execute failed.
	 * @throws InterruptedException if the current thread was interrupted while waiting.
	 * @throws TimeoutException if the wait timed out.
	 * @throws SecurityException - If a security manager exists and its checkExec method doesn't allow creation of the subprocess.
	 * @since 1.0.6
	 */
	public static List<String> executeShell(String[] cmds, String charset, Long timeout) throws IOException, InterruptedException, TimeoutException {
		return executeShell(cmds, charset, timeout, false, true);
	}

	/**
	 * Execute the specified shell command within timeout.
	 * 
	 * @param cmds - the command to execute
	 * @param charset - the charset to encoding
	 * @param timeout - the execute timeout
	 * @param ignoreExitValue - ignore exit value
	 * @param skipEmptyLines - skip empty lines
	 * @return the output strings.
	 * @throws IOException if execute failed.
	 * @throws InterruptedException if the current thread was interrupted while waiting.
	 * @throws TimeoutException if the wait timed out.
	 * @throws SecurityException - If a security manager exists and its checkExec method doesn't allow creation of the subprocess.
	 * @since 1.0.6
	 */
	public static List<String> executeShell(String[] cmds, String charset, Long timeout, boolean ignoreExitValue, boolean skipEmptyLines) throws IOException, InterruptedException, TimeoutException {
		return executeCommand(getShellCommand(cmds), charset, timeout, ignoreExitValue, skipEmptyLines);
	}

	/**
	 * Generate shell command.
	 */
	private static String[] getShellCommand(String... cmds) {
		String[] cmdsh = null;
		if (OperationSystem.getCurrentOS().isLinux()) {
			cmdsh = new String[] { "/bin/sh", "-c" };
		} else if (OperationSystem.getCurrentOS().isWindows()) {
			cmdsh = new String[] { "cmd.exe", "/C" };
		} else {
			throw new UnsupportedOperationException("Unknown operation system.");
		}
		String[] newCmds = new String[cmdsh.length + cmds.length];
		System.arraycopy(cmdsh, 0, newCmds, 0, cmdsh.length);
		System.arraycopy(cmds, 0, newCmds, cmdsh.length, cmds.length);
		return newCmds;
	}

	/**
	 * Execute the specified command without timeout. The process exit
	 * value must be 0, and the empty lines will be skipped.
	 * 
	 * @param cmd - the command to execute
	 * @return the output strings.
	 * @throws IOException if execute failed.
	 * @throws InterruptedException if the current thread was interrupted while waiting.
	 * @throws SecurityException - If a security manager exists and its checkExec method doesn't allow creation of the subprocess.
	 * @since 1.0.1
	 */
	public static List<String> executeCommand(String... cmd) throws IOException, InterruptedException {
		return executeCommand(cmd, DEFAULT_ENCODING);
	}

	/**
	 * Execute the specified command without timeout. The process exit
	 * value must be 0, and the empty lines will be skipped.
	 * 
	 * @param cmd - the command to execute
	 * @param charset - the charset to encoding
	 * @return the output strings.
	 * @throws IOException if execute failed.
	 * @throws InterruptedException if the current thread was interrupted while waiting.
	 * @throws SecurityException - If a security manager exists and its checkExec method doesn't allow creation of the subprocess.
	 * @since 1.0.6
	 */
	public static List<String> executeCommand(String[] cmd, String charset) throws IOException, InterruptedException {
		return executeCommand(cmd, charset, false, true);
	}

	/**
	 * Execute the specified command without timeout.
	 * 
	 * @param cmd - the command to execute
	 * @param charset - the charset to encoding
	 * @param ignoreExitValue - ignore exit value
	 * @param skipEmptyLines - skip empty lines
	 * @return the output strings.
	 * @throws IOException if execute failed.
	 * @throws InterruptedException if the current thread was interrupted while waiting.
	 * @throws SecurityException - If a security manager exists and its checkExec method doesn't allow creation of the subprocess.
	 * @since 1.0.6
	 */
	public static List<String> executeCommand(String[] cmd, String charset, boolean ignoreExitValue, boolean skipEmptyLines) throws IOException, InterruptedException {
		Process p = null;
		try {
			p = Runtime.getRuntime().exec(cmd);
			p.waitFor();
			return readLines(p, charset, ignoreExitValue, skipEmptyLines);
		} finally {
			if (p != null) {
				p.destroy();
			}
		}
	}

	/**
	 * Execute the specified command within the timeout. The process exit
	 * value must be 0, and the empty lines will be skipped.
	 * 
	 * @param cmd - the command to execute
	 * @param timeout - the execute timeout
	 * @return the output strings.
	 * @throws IOException if execute failed.
	 * @throws InterruptedException if the current thread was interrupted while waiting.
	 * @throws TimeoutException if the wait timed out.
	 * @throws SecurityException - If a security manager exists and its checkExec method doesn't allow creation of the subprocess.
	 * @since 1.0.3
	 */
	public static List<String> executeCommand(String[] cmd, Long timeout) throws IOException, InterruptedException, TimeoutException {
		return executeCommand(cmd, DEFAULT_ENCODING, timeout);
	}

	/**
	 * Execute the specified command within the timeout. The process exit
	 * value must be 0, and the empty lines will be skipped.
	 * 
	 * @param cmd - the command to execute
	 * @param charset - the charset to encoding
	 * @param timeout - the execute timeout
	 * @return the output strings.
	 * @throws IOException if execute failed.
	 * @throws InterruptedException if the current thread was interrupted while waiting.
	 * @throws TimeoutException if the wait timed out.
	 * @throws SecurityException - If a security manager exists and its checkExec method doesn't allow creation of the subprocess.
	 * @since 1.0.6
	 */
	public static List<String> executeCommand(String[] cmd, String charset, Long timeout) throws IOException, InterruptedException, TimeoutException {
		return executeCommand(cmd, charset, timeout, false, true);
	}

	/**
	 * Execute the specified command within the timeout.
	 * 
	 * @param cmd - the command to execute
	 * @param charset - the charset to encoding
	 * @param timeout - the execute timeout
	 * @param ignoreExitValue - ignore exit value
	 * @param skipEmptyLines - skip empty lines
	 * @return the output strings.
	 * @throws IOException if execute failed.
	 * @throws InterruptedException if the current thread was interrupted while waiting.
	 * @throws TimeoutException if the wait timed out.
	 * @throws SecurityException - If a security manager exists and its checkExec method doesn't allow creation of the subprocess.
	 * @since 1.0.6
	 */
	public static List<String> executeCommand(String[] cmd, String charset, Long timeout, boolean ignoreExitValue, boolean skipEmptyLines) throws IOException, InterruptedException, TimeoutException {
		Process p = null;
		try {
			p = Runtime.getRuntime().exec(cmd);
			waitFor(p, timeout);
			return readLines(p, charset, ignoreExitValue, skipEmptyLines);
		} finally {
			if (p != null) {
				p.destroy();
			}
		}
	}

	/**
	 * Read lines from process input stream.
	 * 
	 * @param charset - the charset to encoding
	 * @param ignoreExitValue - ignore exit value
	 * @param skipEmptyLines - skip empty lines
	 * @return the output strings.
	 * @throws IOException if read failed.
	 * @since 1.0.6
	 */
	public static List<String> readLines(Process p, String charset, boolean ignoreExitValue, boolean skipEmptyLines) throws IOException {
		Scanner scanner = null;
		if (charset != null) {
			scanner = new Scanner(p.getInputStream(), charset);
		} else {
			scanner = new Scanner(p.getInputStream());
		}
		List<String> list = new ArrayList<>();
		try {
			int exitValue = p.exitValue();
			if (ignoreExitValue || exitValue == 0) {
				while (scanner.hasNextLine()) {
					String line = scanner.nextLine();
					if (!skipEmptyLines || !line.isEmpty()) {
						list.add(line);
					}
				}
			} else {
				throw new IOException("execute failed, exit value=" + exitValue);
			}
			return list;
		} finally {
			IOUtils.closeQuietly(scanner);
		}
	}

	/**
	 * Wait for process exit.
	 * 
	 * @param p - the specified progress
	 * @param timeout - the time to wait
	 * @throws InterruptedException if the current thread was interrupted while waiting.
	 * @throws TimeoutException if the wait timed out.
	 * @since 1.0.3
	 */
	public static void waitFor(Process p, Long timeout) throws InterruptedException, TimeoutException {
		if (timeout != null) {
			long time = timeout.longValue();
			while (time > 0) {
				long delta = time > 100 ? 100 : time; // NOSONAR
				Thread.sleep(delta);
				time -= delta;
				try {
					p.exitValue();
					return;
				} catch (IllegalThreadStateException e) { // NOSONAR
					continue;
				}
			}
			throw new TimeoutException("execute timeout.");
		} else {
			p.waitFor();
		}
	}

}
