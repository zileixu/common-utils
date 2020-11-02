package com.lmnplace.commonutils.common;

import com.lmnplace.commonutils.registcenter.RegistCenterManager;
import com.lmnplace.commonutils.registcenter.RegistCenterManagerBuilder;
import com.lmnplace.commonutils.registcenter.mode.ServiceNode;

import java.io.Serializable;
import java.util.Objects;

public class  KafkaMessage<T> implements Serializable {
    private MessageType type;
    private MetaData metaData;
    private T data;
    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public MetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(MetaData metaData) {
        this.metaData = metaData;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
    public static <T> KafkaMessage build(MessageType type,T data){
        KafkaMessage msg= new KafkaMessage();
        msg.setType(type);
        msg.setData(data);
        msg.setMetaData(buildMetaData());
        return msg;
    }

    private static MetaData buildMetaData() {
        RegistCenterManager manager= RegistCenterManagerBuilder.getCurrentRegistCenterManager();
        ServiceNode node;
        if(!Objects.isNull(manager)&&!Objects.isNull(node=manager.getCurrentServiceNode())){
            MetaData metaData=  new MetaData();
            metaData.setIp(node.getIp());
            metaData.setPort(node.getPort());
            metaData.setHostName(node.getHostName());
            metaData.setServiceType(node.getServiceType());
            return metaData;
        }else {
            return null;
        }
    }
}
