package com.lmnplace.commonutils.globaluuid.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/*
*## zk服务地址 registcenter.zk.servers=ip:port,ip:prot，默认registcenter.zk.servers=127.0.0.1:2181
registcenter.zk.servers=10.10.4.72:2181
## 会话超时时间，默认为10000毫秒，默认：registcenter.zk.seesionTimeout=10000
## 连接超时时间，默认为永久不超时至到连接成功，默认：registcenter.zk.connTimeout=2147483647
## 重试：连接丢失的情况下，重新连接-1为一直重试，至到连接成功，默认为registcenter.zk.reTryTimeout=-1
## 序列化，默认org.I0Itec.zkclient.serialize.SerializableSerializer,默认为：registcenter.zk.clientSerializer=org.I0Itec.zkclient.serialize.SerializableSerializer
## 获取服务类型的服务节点均衡策略，默认为：registcenter.zk.bizBalanceStrategy=com.seemmo.aicommon.registcenter.zk.strategy.OrderLoopStrategy
* */
@ConfigurationProperties(prefix = "uuid")
@EnableConfigurationProperties
@Component
public class UUIDConf {
    private String builder= "zookeeper";

    public String getBuilder() {
        return builder;
    }

    public void setBuilder(String builder) {
        this.builder = builder;
    }
}
