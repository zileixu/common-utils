package com.lmnplace.commonutils.confcenter;

import com.lmnplace.commonutils.confcenter.conf.CenterConfig;
import com.lmnplace.commonutils.confcenter.zk.ZkConfigCenterManager;
import com.lmnplace.commonutils.exception.NoInitException;
import com.lmnplace.commonutils.registcenter.mode.ServiceNode;

import java.util.Map;
import java.util.Objects;

public class ConfCenterManagerBuilder {
    private static ConfigCenterManager configCenterManager;

    private ConfCenterManagerBuilder() {
    }

    public static ConfigCenterManager buildZkConfCenterManager(CenterConfig conf, ServiceNode serviceNode, Map<String,String> properties) throws Exception {
        switch (conf.getType()) {
            case "zookeeper":
                return configCenterManager = ZkConfigCenterManager.buildClusterManager(conf, serviceNode, properties);
            default:
                return configCenterManager = ZkConfigCenterManager.buildClusterManager(conf, serviceNode, properties);
        }
    }

    public static ConfigCenterManager getCurrentConfCenterManager() {
        if (Objects.isNull(configCenterManager)) {
            throw new NoInitException("ConfigCenterManager is not inited");
        }
        return configCenterManager;
    }
}
