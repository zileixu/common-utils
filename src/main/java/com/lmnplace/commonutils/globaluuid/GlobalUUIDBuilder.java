package com.lmnplace.commonutils.globaluuid;

import com.lmnplace.commonutils.exception.NoInitException;
import com.lmnplace.commonutils.globaluuid.conf.UUIDConf;
import com.lmnplace.commonutils.globaluuid.zk.ZkUUIDGenerater;
import com.lmnplace.commonutils.common.zk.ZkClientSingleton;

import java.util.Objects;

public class GlobalUUIDBuilder {
    private static GlobalUUIDGenerater generater;
    private GlobalUUIDBuilder() {
    }

    public static GlobalUUIDGenerater buildZkUUIDGenerater(UUIDConf uuidConf) {
        if(Objects.isNull(generater)){
            synchronized (GlobalUUIDBuilder.class){
                if(Objects.isNull(generater)){
                    switch (uuidConf.getBuilder()) {
                        case "zookeeper":
                            return ZkUUIDGenerater.buildZkUUIDGenerater(ZkClientSingleton.instance().getZkClient());
                        default:
                            return ZkUUIDGenerater.buildZkUUIDGenerater(ZkClientSingleton.instance().getZkClient());
                    }
                }
            }
        }
        return generater;
    }
    public static GlobalUUIDGenerater instance(){
        if(Objects.isNull(generater)){
            throw new NoInitException("GlobalUUIDGenerater is not inited");
        }
        return generater;
    }
}
