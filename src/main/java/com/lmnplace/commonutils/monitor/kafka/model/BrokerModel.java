package com.lmnplace.commonutils.monitor.kafka.model;

import org.apache.commons.collections.CollectionUtils;
import org.apache.kafka.common.Node;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class BrokerModel {
    private String host;
    private Integer port;

    public BrokerModel() {
    }

    public BrokerModel(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
    public static BrokerModel build(Node node){
        BrokerModel model=new BrokerModel();
        if(Objects.isNull(node)){
            return model;
        }
        model.setHost(node.host());
        model.setPort(node.port());
        return model;

    }
    public static List<BrokerModel> build(List<Node> nodes){
        List<BrokerModel> models= new LinkedList<>();
        if(CollectionUtils.isEmpty(nodes)){
            return models;
        }
        for(Node node:nodes){
            models.add(build(node));
        }
        return models;
    }
}
