package com.github.mirs.banxiaoxiao.framework.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Random;
import java.util.regex.Pattern;

public class NetworkUtil {

    private static final Logger logger = LoggerFactory.getLogger(NetworkUtil.class);

    public static final String LOCALHOST = "127.0.0.1";

    public static final String ANYHOST = "0.0.0.0";

    private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");

    private static volatile InetAddress LOCAL_ADDRESS = null;

    public static boolean isHostReachable(String host) {
        try {
            return InetAddress.getByName(host).isReachable(3000);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getHostNameForLiunx() {
        try {
            return (InetAddress.getLocalHost()).getHostName();
        } catch (UnknownHostException uhe) {
            String host = uhe.getMessage(); // host = "hostname: hostname"
            if (host != null) {
                int colon = host.indexOf(':');
                if (colon > 0) {
                    return host.substring(0, colon);
                }
            }
            return "UnknownHost";
        }
    }

    public static String getHostName() {
        if (System.getenv("COMPUTERNAME") != null) {
            return System.getenv("COMPUTERNAME");
        } else {
            return getHostNameForLiunx();
        }
    }

    /**
     * 获取当前机器的标识: ip_pid_random
     *
     * @return
     */
    public static String aboutThisJvm() {
        StringBuffer sb = new StringBuffer();
        sb.append(getLocalHost());
        // get name representing the running Java virtual machine.
        String name = ManagementFactory.getRuntimeMXBean().getName();
        // get pid
        sb.append("_").append(name.split("@")[0]);
        sb.append("_").append(new Random().nextInt(100));
        return sb.toString();
    }

    /**
     * 如果获取网络地址失败，返回本地回环地址：127.0.0.1
     *
     * @return 本地地址
     */
    public static String getLocalHost() {
        InetAddress address = getLocalAddress();
        return address == null ? LOCALHOST : address.getHostAddress();
    }

    public static boolean isValidNetAddress(String ipAddress) {
        return (ipAddress != null && !ANYHOST.equals(ipAddress) && !LOCALHOST.equals(ipAddress) && IP_PATTERN.matcher(ipAddress).matches());
    }

    public static boolean isValidIpAddress(String ipAddress) {
        return (ipAddress != null && IP_PATTERN.matcher(ipAddress).matches());
    }

    /**
     * 遍历本地网卡，返回第一个合理的IP。
     *
     * @return 本地网卡IP
     */
    private static InetAddress getLocalAddress() {
        if (LOCAL_ADDRESS != null)
            return LOCAL_ADDRESS;
        InetAddress localAddress = findFirstNonLoopbackAddress();
        LOCAL_ADDRESS = localAddress;
        return localAddress;
    }

    private static InetAddress findFirstNonLoopbackAddress() {
        InetAddress result = null;
        try {
            // 记录网卡最小索引
            int lowest = Integer.MAX_VALUE;
            // 获取所有网卡
            for (Enumeration<NetworkInterface> nics = NetworkInterface.getNetworkInterfaces(); nics.hasMoreElements();) {
                NetworkInterface ifc = nics.nextElement();
                if (ifc.isUp()) {
                    logger.trace("Testing interface: " + ifc.getDisplayName());
                    if (ifc.getIndex() < lowest || result == null) {
                        lowest = ifc.getIndex(); // 记录索引
                    } else if (result != null) {
                        continue;
                    }
                    for (Enumeration<InetAddress> addrs = ifc.getInetAddresses(); addrs.hasMoreElements();) {
                        InetAddress address = addrs.nextElement();
                        if (address instanceof Inet4Address && !address.isLoopbackAddress()) {
                            logger.trace("Found non-loopback interface: " + ifc.getDisplayName());
                            result = address;
                        }
                    }
                }
            }
        } catch (IOException ex) {
            logger.error("Cannot get first non-loopback address", ex);
        }
        if (result != null) {
            return result;
        }
        try {
            return InetAddress.getLocalHost(); // 如果以上逻辑都没有找到合适的网卡，则使用JDK的InetAddress.getLocalhost()
        } catch (UnknownHostException e) {
            logger.warn("Unable to retrieve localhost");
        }
        return null;
    }

    private static boolean isValidAddress(InetAddress address) {
        if (address == null || address.isLoopbackAddress()) {
            return false;
        }
        String name = address.getHostAddress();
        return isValidNetAddress(name);
    }
}
