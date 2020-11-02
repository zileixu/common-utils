package com.lmnplace.commonutils.registcenter.mode;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;
import java.io.Serializable;

public class OpsCmd implements Serializable {
    @Min(0)
    private int sshdPort = 22;//SSH口号
    private String opsUserName; //系统用户名密码
    private String opsUserPwd;  //系统用户密码
    @NotBlank
    private String binDir;
    @NotBlank
    private String startCmd;
    @NotBlank
    private String stopCmd;
    @NotBlank
    private String statusCmd;
    @NotBlank
    private String restartCmd;

    public String getCmd(ServiceOpsType type){
         switch (type){
             case START:
                 return getStartCmd();
             case STOP:
                 return getStopCmd();
             case RESTART:
                 return getRestartCmd();
             case STATUS:
                 return getStatusCmd();
             default:
                 throw new RuntimeException(type.name()+"操作不支持");
         }
    }
    public int getSshdPort() {
        return sshdPort;
    }

    public void setSshdPort(int sshdPort) {
        this.sshdPort = sshdPort;
    }

    public String getOpsUserName() {
        return opsUserName;
    }

    public void setOpsUserName(String opsUserName) {
        this.opsUserName = opsUserName;
    }

    public String getOpsUserPwd() {
        return opsUserPwd;
    }

    public void setOpsUserPwd(String opsUserPwd) {
        this.opsUserPwd = opsUserPwd;
    }

    public String getBinDir() {
        return binDir;
    }

    public void setBinDir(String binDir) {
        this.binDir = binDir;
    }

    public String getStartCmd() {
        return startCmd;
    }

    public void setStartCmd(String startCmd) {
        this.startCmd = startCmd;
    }

    public String getStopCmd() {
        return stopCmd;
    }

    public void setStopCmd(String stopCmd) {
        this.stopCmd = stopCmd;
    }

    public String getStatusCmd() {
        return statusCmd;
    }

    public void setStatusCmd(String statusCmd) {
        this.statusCmd = statusCmd;
    }

    public String getRestartCmd() {
        return restartCmd;
    }

    public void setRestartCmd(String restartCmd) {
        this.restartCmd = restartCmd;
    }
}
