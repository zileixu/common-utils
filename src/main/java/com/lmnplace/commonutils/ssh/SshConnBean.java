package com.lmnplace.commonutils.ssh;

import net.neoremind.sshxcute.core.SysConfigOption;
import org.apache.commons.lang3.StringUtils;

public class SshConnBean {

    private String host;
    private String user;
    private Integer port = SysConfigOption.SSH_PORT_NUMBER;
    private String password;
    private String pubKeyPath;
    private boolean nopassLogin = false;

    public SshConnBean(String host, String user, String password) {
        this.host = host;
        this.password = password;
        this.user = user;
    }

    public SshConnBean(String host, Integer port, String user, String password) {
        this.host = host;
        this.password = password;
        this.user = user;
        this.port = port;
    }

    public SshConnBean(String host, Integer port, String user, String password, String pubKeyPath) {
        this.host = host;
        this.user = user;
        this.port = port;
        this.password = password;
        this.pubKeyPath = pubKeyPath;
    }

    public String getPubKeyPath() {
        return pubKeyPath;
    }

    public void setPubKeyPath(String pubKeyPath) {
        this.nopassLogin = true;
        this.pubKeyPath = pubKeyPath;
    }

    public boolean isNopassLogin() {
        if (StringUtils.isNotBlank(this.password)) {
            return false;
        } else {
            return true;
        }
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}