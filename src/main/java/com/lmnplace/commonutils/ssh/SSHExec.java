package com.lmnplace.commonutils.ssh;

import com.jcraft.jsch.*;
import net.neoremind.sshxcute.core.ConnCredential;
import net.neoremind.sshxcute.core.Result;
import net.neoremind.sshxcute.core.SSHExecUtil;
import net.neoremind.sshxcute.core.SysConfigOption;
import net.neoremind.sshxcute.exception.TaskExecFailException;
import net.neoremind.sshxcute.task.CustomTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This is the core class to connect to remote machine and execute commands on
 * it. <br>
 * <p>
 * This class is implemented with singleton pattern, you can only retrieve one
 * instance of SSH connection.
 * <p>
 * To get the object, you should first init a ConnBean and then pass it as args
 * like below
 * <p>
 * ConnBean cb = new ConnBean("rfidic-1.svl.ibm.com", "tsadmin", "u7i8o9p0");
 * <p>
 * SSHExec ssh = SSHExec.getInstance(cb);
 * <p>
 * <p>
 * 1. Before you begin to execute command or upload files to server, you should
 * connect to server:
 * <p>
 * ssh.connect();
 * <p>
 * <p>
 * 2. SSHExec provides a way to execute command and upload files to remote
 * machine. <br>
 * 1) How to execute a task
 * <p>
 * You should first create the TASK class which extends from
 * com.ibm.rfidic.test.task.CustomCode class, please refer to below example:
 * <p>
 * CustomCode startRFIDIC = new StartRFIDIC("WAS,CAPTURE");
 * <p>
 * ssh.execCmd(startRFIDIC);
 * <p>
 * 2) How to upload all the files that under a specified folder
 * <p>
 * If you want to upload data/FVT_SETUP_001/*.* to ~/UIFVT directory on remote
 * machine, please refer to below code:
 * <p>
 * ssh.uploadAllDataToServer("data/FVT_SETUP_001", "~/UIFVT");
 * <p>
 * 3) How to upload single one files
 * <p>
 * ssh.uploadSingleDataToServer("data/ui/event/group1_events.xml", "~/UIdata");
 * <p>
 * <p>
 * 3. After all task finish, you should disconnect and end the session.
 * <p>
 * ssh.disconnect();
 *
 * @author zxucdl
 */
public class SSHExec {
    static Logger logger = LoggerFactory.getLogger(SSHExec.class);

    private Session session;

    private Channel channel;

    private SshConnBean conn;
    private JSch jsch;
    public SSHExec(SshConnBean conn) {
        try {
            logger.info("SSHExec initializing ...");
            this.conn = conn;
            jsch = new JSch();
        } catch (Exception e) {
            logger.error("Init SSHExec fails with the following", e);
        }
    }

    /**
     * Singleton pattern to get one instance of SSHExec class
     *
     * @param conn
     * @return SSHExec instance
     */
	/*public static SSHExec getInstance(ConnBean conn) {
		if (ssh == null) {
			ssh = new SSHExec(conn);
		}
		return ssh;
	}*/

    /**
     * Connect to remote machine and establish session.
     *
     * @return if connect successfully, return true, else return false
     */
    public Boolean connect() {
        try {
            if (conn.isNopassLogin()) {
                jsch.addIdentity(conn.getPubKeyPath());
                session = jsch.getSession(conn.getUser(), conn.getHost(), conn.getPort());
                java.util.Properties config = new java.util.Properties();
                config.put("StrictHostKeyChecking", "no");
                session.setConfig(config);
            } else {
                session = jsch.getSession(conn.getUser(), conn.getHost(), conn.getPort());
                UserInfo ui = new ConnCredential(conn.getPassword());
                logger.debug("Session initialized and associated with user credential " + conn.getPassword());
                session.setUserInfo(ui);
            }
            logger.info("SSHExec initialized successfully");
            logger.info("SSHExec trying to connect " + conn.getUser() + "@" + conn.getHost());
            session.connect(120000);
            logger.info("SSH connection established");
        } catch (Exception e) {
            logger.error("Connect fails with the following exception ", e);
            return false;
        }
        return true;
    }

    /**
     * Disconnect to remote machine and destroy session.
     *
     * @return if disconnect successfully, return true, else return false
     */
    public Boolean disconnect() {
        try {
            session.disconnect();
            session = null;
            logger.info("SSH connection shutdown");
        } catch (Exception e) {
            logger.error("Disconnect fails with the following exception", e);
            return false;
        }
        return true;
    }

    /**
     * Execute task on remote machine
     *
     * @param task - Task object that extends from CustomCode
     * @throws Exception
     */
    public synchronized Result exec(CustomTask task) throws TaskExecFailException {
        Result r = new Result();
        try {
            channel = session.openChannel("exec");
            String command = task.getCommand();
            logger.info("Command is " + command);
            ((ChannelExec) channel).setCommand(command);
            // X Forwarding
            // channel.setXForwarding(true);
            // channel.setInputStream(System.in);
            channel.setInputStream(null);

            channel.setOutputStream(System.out);

            FileOutputStream fos = new FileOutputStream(SysConfigOption.ERROR_MSG_BUFFER_TEMP_FILE_PATH);
            ((ChannelExec) channel).setErrStream(fos);
            // ((ChannelExec) channel).setErrStream(System.err);
            InputStream in = channel.getInputStream();
            channel.connect();
            logger.info("Connection channel established succesfully");
            logger.info("Start to run command");
            StringBuilder sb = new StringBuilder();
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0)
                        break;
                    String str = new String(tmp, 0, i);
                    sb.append(str);
                    logger.info(str);
                }
                if (channel.isClosed()) {
                    logger.info("Connection channel closed");
                    //logger.putMsg(Logger.INFO,"Exit-status: " + channel.getExitStatus());
                    logger.info("Check if exec success or not ... ");
                    r.rc = channel.getExitStatus();
                    r.sysout = sb.toString();
                    if (task.isSuccess(sb.toString(), channel.getExitStatus())) {
                        logger.info("Execute successfully for command: " + task.getCommand());
                        r.error_msg = "";
                        r.isSuccess = true;
                    } else {
                        r.error_msg = SSHExecUtil.getErrorMsg(SysConfigOption.ERROR_MSG_BUFFER_TEMP_FILE_PATH);
                        r.isSuccess = false;
                        logger.info("Execution failed while executing command: " + task.getCommand());
                        logger.info("Error message: " + r.error_msg);
                        if (SysConfigOption.HALT_ON_FAILURE) {
                            logger.error("The task has failed to execute :" + task.getInfo() + ". So program exit.");
                            throw new TaskExecFailException(task.getInfo());
                        }
                    }
                    break;
                }

            }
            try {
                logger.info("Now wait " + SysConfigOption.INTEVAL_TIME_BETWEEN_TASKS / 1000 + " seconds to begin next task ...");
                Thread.sleep(SysConfigOption.INTEVAL_TIME_BETWEEN_TASKS);
            } catch (Exception ee) {
            }
            channel.disconnect();
            logger.info("Connection channel disconnect");
        } catch (JSchException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            logger.error("exec {}", task != null ? task.getCommand() : "", e);
        }
        return r;
    }


}