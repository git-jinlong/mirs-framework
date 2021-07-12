package com.github.mirs.banxiaoxiao.framework.common.ssh;


import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * java ssh utils
 *
 * @author bc
 */
@Slf4j
public class SSHHelper {

    private Session session;

    public SSHHelper(String host, int port, String user, String pass) {
        connect(host, port, user, pass);
    }

    /**
     * 连接远程主机，获取远程主机的session信息，用于后续操作
     *
     * @param host ip
     * @param port ssh port
     * @param user ssh user
     * @param pass ssh password
     * @return remote login session
     */
    private Session connect(String host, Integer port, String user, String pass) {

        try {
            JSch jsch = new JSch();

            if (null == port) {
                session = jsch.getSession(user, host);
            } else {
                session = jsch.getSession(user, host, port);
            }

            session.setPassword(pass);
            //设置第一次登陆的时候提示，可选值:(ask | yes | no)
            session.setConfig("StrictHostKeyChecking", "no");
            //30秒连接超时
            session.connect(30000);

        } catch (Exception ex) {
            log.error("ssh connect host={},port={},user={},error", host, port, user, ex);

        }

        return session;

    }

    /**
     * 执行命令
     *
     * @param command 待执行的命令
     * @return {@link SSHResInfo}
     */
    public SSHResInfo sendCmd(String command) throws IOException, JSchException {
        return sendCmd(command, 3000);
    }


    /**
     * 执行命令
     *
     * @param command 待执行的命令
     * @param delay   等待时间
     * @return {@link SSHResInfo}
     */
    public SSHResInfo sendCmd(String command, int delay) throws JSchException, IOException {

        if (null == session) {
            return null;
        }

        log.info("sendCmd cmd={} , delay={}", command, delay);
        if (delay < 100) {
            delay = 100;
        }
        SSHResInfo info = null;
        //设置临时缓冲区
        byte[] b = new byte[1024];
        //接收ssh返回的结果

        List<String> out = new ArrayList<>();
        List<String> err = new ArrayList<>();

        Channel channel = session.openChannel("exec");

        ChannelExec ssh = (ChannelExec) channel;

        ssh.setErrStream(System.err);

        //返回的结果可能是标准信息,也可能是错误信息,所以两种输出都要获取
        //一般情况下只会有一种输出.
        //但并不是说错误信息就是执行命令出错的信息,如获得远程java JDK版本就以
        //ErrStream来获得.
        InputStream stdStream = ssh.getInputStream();
        InputStream errStream = ssh.getErrStream();
        ssh.setCommand(command);
        ssh.connect();
        try {

            //开始获得SSH命令的结果
            while (true) {
                //获得错误输出
                while (errStream.available() > 0) {
                    int i = errStream.read(b, 0, 1024);
                    if (i < 0) {
                        break;
                    }
                    err.add(new String(b, 0, i));
                }

                //获得标准输出
                while (stdStream.available() > 0) {
                    int i = stdStream.read(b, 0, 1024);
                    if (i < 0) {
                        break;
                    }
                    out.add(new String(b, 0, i));
                }
                if (ssh.isClosed()) {
                    int code = ssh.getExitStatus();
                    log.info("exit-code: " + code);
                    info = new SSHResInfo(code, out, err);
                    break;
                }
                try {
                    Thread.sleep(delay);
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            }
        } finally {
            channel.disconnect();

        }

        return info;
    }

    /**
     * 退出当前登陆信息
     */
    public void close() {
        if (null != session) {
            session.disconnect();
        }
    }

//  public static void main(String[] args) throws IOException, JSchException {
//    SSHHelper sshHelper = new SSHHelper("172.17.228.245", 22, "root", "master007");
//
////    sshHelper.sendCmd("echo  \"/mnt/data/remote/iface/  2(rw,sync,no_root_squash)\\\n\" >> /etc/exports", 0);
////    sshHelper.sendCmd("echo  \"/mnt/data/remote/iface/  3(rw,sync,no_root_squash)\\\n\" >> /etc/exports", 0);
////    sshHelper.sendCmd("echo  \"b\" >> /etc/exports", 0);
////    sshHelper.sendCmd("echo  \"c\" >> /etc/exports", 0);
//
////    SSHResInfo info = sshHelper.sendCmd("ip route | grep 172.17.228.245 | awk -F '[ \\t*]' '{print $3}'");
//    SSHResInfo info = sshHelper.sendCmd("date", 0);
//    System.out.println(info.toString());
//    sshHelper.close();

//    String res = "[{\"ip\":\"172.17.81.21\",\"user\":\"root\",\"pass\":\"master007\",\"port\":22}]";
//    byte [] a = res.getBytes("UTF-8");
//    String encode = Base64.getEncoder().encodeToString(a);
//
//    System.out.println(encode);
//    byte [] b = Base64.getDecoder().decode(encode.getBytes());
//
//    System.out.println(new String(b,"utf-8"));

//  }

}
