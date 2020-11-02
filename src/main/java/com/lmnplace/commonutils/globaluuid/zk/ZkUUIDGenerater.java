package com.lmnplace.commonutils.globaluuid.zk;

import com.lmnplace.commonutils.common.constants.CommonConstants;
import com.lmnplace.commonutils.globaluuid.GlobalUUIDGenerater;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;

import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;

public class ZkUUIDGenerater implements GlobalUUIDGenerater {
    private static ZkUUIDGenerater zkUUIDGenerater;
    private static ZkClient zkClient;
    private static String uuidRootPath="GLOBALUUID";
    private static final LinkedBlockingQueue<String> historyUUIDs = new LinkedBlockingQueue();
    private ZkUUIDGenerater(){}
    public static ZkUUIDGenerater  buildZkUUIDGenerater(ZkClient zkClient){
        if(!Objects.isNull(zkUUIDGenerater)){
            return  zkUUIDGenerater;
        }
        synchronized (historyUUIDs){
            if(!Objects.isNull(zkUUIDGenerater)){
                return  zkUUIDGenerater;
            }
            ZkUUIDGenerater uuidGenerater=new ZkUUIDGenerater();
            uuidGenerater.zkClient=zkClient;
            initClearUUIDHistroy();
            zkUUIDGenerater=uuidGenerater;
            return zkUUIDGenerater;
        }
    }
    @Override
    public String genUUID() {
        if (!zkClient.exists(uuidRootPath)) {
            zkClient.create(uuidRootPath, null, CreateMode.PERSISTENT);
        }
        String uuid = zkClient.create(String.format("%s%suuid", uuidRootPath, CommonConstants.CommonFlag.RIGHTSLASH.getVal()), null, CreateMode.PERSISTENT_SEQUENTIAL);
        if (!Objects.isNull(uuid)) {
            historyUUIDs.offer(uuid);
        }
        return uuid.substring(uuid.lastIndexOf(CommonConstants.CommonFlag.RIGHTSLASH.getVal())+1);
    }

    private static void initClearUUIDHistroy() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true){
                        String uuid = historyUUIDs.take();
                        zkClient.delete(uuid);
                    }
                } catch (InterruptedException e) {
                }

            }
        }).start();
    }
}
