package com.lmnplace.commonutils.confcenter.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "confcenter")
@EnableConfigurationProperties
@Component
public class CenterConfig {
    /**
     * 启动连接配置中心，即使用配置中心同步配置功能
     */
    private boolean enableConn;
    /**
     * 标识为配置中心管理节点，配置中心管理及诶单有权对服务共享配置进行管理
     */
    private boolean enableManager;
    /**
     * 配置中心类型
     */
    private String type= "zookeeper";
    /**
     * 启动服务时，强制重置共享配置
     */
    private boolean startServiceForceResetShareConf;
    /**
     * 强制使用本地（服务实例自己的）配置
     */
    private boolean forceUseSelfConf;

    public boolean isForceUseSelfConf() {
        return forceUseSelfConf;
    }

    public void setForceUseSelfConf(boolean forceUseSelfConf) {
        this.forceUseSelfConf = forceUseSelfConf;
    }

    public boolean isEnableConn() {
        return enableConn;
    }

    public void setEnableConn(boolean enableConn) {
        this.enableConn = enableConn;
    }


    public boolean isEnableManager() {
        return enableManager;
    }

    public void setEnableManager(boolean enableManager) {
        this.enableManager = enableManager;
    }

    public boolean isStartServiceForceResetShareConf() {
        return startServiceForceResetShareConf;
    }

    public void setStartServiceForceResetShareConf(boolean startServiceForceResetShareConf) {
        this.startServiceForceResetShareConf = startServiceForceResetShareConf;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
