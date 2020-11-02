package com.lmnplace.commonutils.registcenter;

import com.lmnplace.commonutils.exception.NoInitException;
import com.lmnplace.commonutils.registcenter.conf.RegisterCenterConf;
import com.lmnplace.commonutils.registcenter.zk.ZkRegistCenterManager;
import com.lmnplace.commonutils.exception.ConfParamsValidationException;
import com.lmnplace.commonutils.registcenter.mode.ServiceNode;
import com.lmnplace.commonutils.common.zk.ZkClientSingleton;

import java.util.Objects;

public class RegistCenterManagerBuilder {
    private static RegistCenterManager  registCenterManager;
    private RegistCenterManagerBuilder() {
    }

    public static RegistCenterManager buildZkRegistCenterManager(RegisterCenterConf conf, ServiceNode serviceNode) throws ClassNotFoundException, InstantiationException, ConfParamsValidationException, IllegalAccessException {
        if(!conf.isEnable()){
            return null;
        }
        switch (conf.getType()){
            case "zookeeper":
                return registCenterManager = ZkRegistCenterManager.buildClusterManager(conf,serviceNode,ZkClientSingleton.instance());
            default:
                return registCenterManager =ZkRegistCenterManager.buildClusterManager(conf,serviceNode,ZkClientSingleton.instance());
        }
    }
    public static RegistCenterManager getCurrentRegistCenterManager(){
        if(Objects.isNull(registCenterManager)){
            throw new NoInitException("RegistCenterManager is not inited");
        }
        return registCenterManager;
    }
}
