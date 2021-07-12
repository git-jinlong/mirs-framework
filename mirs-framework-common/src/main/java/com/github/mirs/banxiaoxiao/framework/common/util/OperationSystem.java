package com.github.mirs.banxiaoxiao.framework.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Operation System.
 *
 * @author fjli
 * @since 1.0.1
 */
public enum OperationSystem {

  OS_UNKNOWN, OS_WINDOWS, OS_CENTOS, OS_REDHAT, OS_UBUNTU, OS_FEDORA, OS_DEBIAN, OS_SUSE;

  private static final Logger LOG = LoggerFactory.getLogger(OperationSystem.class);
  private static OperationSystem os = null;

  /**
   * Test the OS is linux OS or not.
   *
   * @since 1.0.3
   */
  public boolean isLinux() {
    return this != OS_WINDOWS && this != OS_UNKNOWN;
  }

  /**
   * Test the OS is windows or not.
   *
   * @since 1.0.3
   */
  public boolean isWindows() {
    return this == OS_WINDOWS;
  }

  /**
   * Get current operation system.
   */
  public static OperationSystem getCurrentOS() {
    if (os != null) {
      return os;
    }
    String name = System.getProperty("os.name");
    if (name != null) {
      name = name.toLowerCase();
      if (name.contains("windows")) {
        os = OS_WINDOWS;
      } else if (name.contains("linux")) {
        os = getLinuxOs();
      }
    }
    if (os == null) {
      os = OS_UNKNOWN;
    }
    return os;
  }

  /**
   * Get LINUX operation system.
   */
  private static OperationSystem getLinuxOs() {
    String version = readVersion();
    if (version == null) {
      return OS_UNKNOWN;
    } else if (version.contains("centos")) {
      return OS_CENTOS;
    } else if (version.contains("redhat")) {
      return OS_REDHAT;
    } else if (version.contains("ubuntu")) {
      return OS_UBUNTU;
    } else if (version.contains("debian")) {
      return OS_DEBIAN;
    } else if (version.contains("fedora")) {
      return OS_FEDORA;
    } else if (version.contains("suse")) {
      return OS_SUSE;
    } else {
      return OS_UNKNOWN;
    }
  }

  private static String readVersion() {
    try {
      Path path = Paths.get("/proc/version");
      Charset cs = Charset.defaultCharset();
      List<String> lines = Files.readAllLines(path, cs);
      if (lines == null || lines.isEmpty()) {
        return null;
      }
      return lines.get(0).toLowerCase();
    } catch (Exception e) {
      LOG.error("read version info failed.", e);
      return null;
    }
  }

}
