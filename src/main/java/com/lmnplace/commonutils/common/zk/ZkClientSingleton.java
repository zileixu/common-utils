package com.lmnplace.commonutils.common.zk;

import com.alibaba.fastjson.JSON;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class ZkClientSingleton{
    private static final ZkClientSingleton zkClientSingleton=new ZkClientSingleton();
    private static ZkClient zkClient;
    private static ZkConf zkConf;
    private ZkClientSingleton(){}
    public static ZkClientSingleton buildZkClient(ZkConf zkConf) throws Exception{
        if(!Objects.isNull(zkClient)){
            return  zkClientSingleton;
        }
        synchronized (zkClientSingleton){
            if(!Objects.isNull(zkClient)){
                return  zkClientSingleton;
            }
            ZkSerializer zkSerializer = (ZkSerializer) (StringUtils.isBlank(zkConf.getClientSerializer()) ? new SerializableSerializer() : Class.forName(zkConf.getClientSerializer()).newInstance());
            ZkClientSingleton.zkClient = new ZkClient(new ZkConnection(zkConf.getServers(), zkConf.getSeesionTimeout()), zkConf.getConnTimeout(),
                    zkSerializer, zkConf.getReTryTimeout());
            ZkClientSingleton.zkConf=zkConf;
            return zkClientSingleton;
        }
    }
    public static ZkClientSingleton instance(){
        if(Objects.isNull(zkClientSingleton.getZkClient())){
           throw new RuntimeException("zkClient is not inited!");
        }
        return zkClientSingleton;
    }

    public ZkClient getZkClient() {
        return zkClient;
    }

    public ZkConf getZkConf() {
        return zkConf;
    }

    public static void main(String[] args) throws Exception {
        ZkConf zkConf=new ZkConf();
        zkConf.setServers("10.10.4.49:2181");
        ZkClient zkClient=ZkClientSingleton.buildZkClient(zkConf).getZkClient();
        System.out.println(JSON.toJSONString(zkClient.readData("/test")));
    }
}
