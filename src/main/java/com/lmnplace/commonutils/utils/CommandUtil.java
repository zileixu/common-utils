package com.lmnplace.commonutils.utils;

import com.alibaba.fastjson.JSON;
import com.lmnplace.commonutils.ssh.SSHExec;
import com.lmnplace.commonutils.ssh.SshConnBean;
import net.neoremind.sshxcute.core.Result;
import net.neoremind.sshxcute.core.SysConfigOption;
import net.neoremind.sshxcute.task.CustomTask;
import net.neoremind.sshxcute.task.impl.ExecCommand;
import org.apache.commons.exec.*;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

public class CommandUtil {
    private static final String LOCAL127 = "127.0.0.1";
    private static final String DEFAULT_CHARSET = "UTF-8";
    private static Logger logger = LoggerFactory.getLogger(CommandUtil.class);

    /**
     * 执行指定命令
     *
     * @param commands 命令
     * @return 命令执行完成返回结果
     * @throws IOException 失败时抛出异常，由调用者捕获处理
     */
    public static CmdResult exeCommand(String... commands) throws IOException {
        String cmds=JSON.toJSONString(commands);
        logger.info("Start to execute cmd:{} for local", cmds);
        try (
                ByteArrayOutputStream out = new ByteArrayOutputStream();
        ) {
            if (ArrayUtils.isEmpty(commands)) {
                return new CmdResult(false, "cmd cannot be blank");
            }
            int length = commands.length;
            for (int i = 0; i < length; i++) {
                boolean isSuccess;
                if(commands[i].endsWith("&")){
                    asyncExeCommand(commands[i]);
                    isSuccess =true;
                }else {
                    isSuccess = exeCommand(commands[i], out);
                }
                if (isSuccess && i < length - 1) {
                    continue;
                } else if (isSuccess && i == length - 1) {
                    return new CmdResult(true, out.toString(DEFAULT_CHARSET));
                } else {
                    return new CmdResult(false, out.toString(DEFAULT_CHARSET));
                }
            }
            return new CmdResult(false, "Shouldnot reach here");
        }finally {
            logger.info("End to execute cmd:{} for local", cmds);
        }
    }

    /**
     * 远程执行指定命令
     *
     * @param host
     * @param port
     * @param user
     * @param passwd
     * @param pubKeyPath
     * @param commands
     * @return
     * @throws IOException
     */
    public static CmdResult sshExeCommand(String host, Integer port, String user, String passwd, String pubKeyPath, String... commands) throws IOException {
        if (StringUtils.isBlank(host) || IpUtil.getHostIp().equals(host) || IpUtil.getHostName().equals(host) || LOCAL127.equals(host) ) {
            return exeCommand(commands);
        }
        if(Objects.isNull(port)){
            port= SysConfigOption.SSH_PORT_NUMBER;
        }
        if(StringUtils.isBlank(user)){
            return new CmdResult(false, "SSH用户名不能为空");
        }
        if(StringUtils.isBlank(passwd)&&StringUtils.isBlank(pubKeyPath)){
            return new CmdResult(false, "SSH用户密码或公钥不允许全为空");
        }
       String cmds = JSON.toJSONString(commands);
        logger.info("Start to execute cmd:{} for ssh", cmds);
        SSHExec sshExec = null;
        try {
            SshConnBean sshConnBean = new SshConnBean(host, port, user, passwd, pubKeyPath);
            sshExec = new SSHExec(sshConnBean);
            CustomTask cTask = new ExecCommand(commands);
            sshExec.connect();
            Result result = sshExec.exec(cTask);
            if (result.isSuccess) {
                return new CmdResult(true, result.sysout);
            } else {
                return new CmdResult(false, result.error_msg);
            }
        } catch (Exception e) {
            logger.error("To sshExeCommand is fail ,cmd:{}", cmds, e);
            return new CmdResult(true, e.getMessage());
        } finally {
            if (sshExec != null) {
                sshExec.disconnect();
            }
            logger.info("End to execute cmd:{} for ssh", cmds);
        }
    }
    /**
     * 执行指定命令，输出结果到指定输出流中
     *
     * @param command 命令
     * @param out     执行结果输出流
     * @return 执行结果状态码：执行成功返回0
     * @throws ExecuteException 失败时抛出异常，由调用者捕获处理
     * @throws IOException      失败时抛出异常，由调用者捕获处理
     */
    public static boolean exeCommand(String command, OutputStream out) throws ExecuteException, IOException {
        CommandLine commandLine = CommandLine.parse(command);
        PumpStreamHandler pumpStreamHandler = null;
        if (null == out) {
            pumpStreamHandler = new PumpStreamHandler();
        } else {
            pumpStreamHandler = new PumpStreamHandler(out);
        }
        // 设置超时时间为10秒
        ExecuteWatchdog watchdog = new ExecuteWatchdog(10000);
        DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(pumpStreamHandler);
        executor.setWatchdog(watchdog);
        return executor.execute(commandLine)==0?true:false;
    }
    /**
     * 异步执行指定命令
     * @param command
     * @param handler
     * @throws IOException
     */
    public static void asyncExeCommand(String command, ExecuteResultHandler handler) throws IOException {
        CommandLine commandLine = CommandLine.parse(command);
        DefaultExecutor executor = new DefaultExecutor();
        executor.execute(commandLine,handler);
    }
    /**
     * 异步执行指定命令
     * @param command
     * @throws IOException
     */
    public static void asyncExeCommand(String command) throws IOException {
        asyncExeCommand(command,new ExecuteResultHandler(){

            @Override
            public void onProcessComplete(int exitValue) {
                logger.info("onProcessComplete cmd:{},exit status:{}",command,exitValue);
            }

            @Override
            public void onProcessFailed(ExecuteException e) {
                logger.error("onProcessFailed cmd:{}",command,e);
            }
        });
    }
}
