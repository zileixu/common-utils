package com.lmnplace.commonutils.common.kafka.conf;


import com.lmnplace.commonutils.utils.StringUtil;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Properties;

public class ProducerConf {
    private String bootstrapServers;
    /**
     * 每个消息仅发送一次，避免重复发送
     */
    private Boolean enableIdempotence=true;
    /**
     * 批量发送数据大小，达到次阈值立即发送
     */
    private Integer batchSize;
    /**
     * 数据发送延迟时间，结合batchSize使用
     */
    private Integer lingerMs;
    private Integer requestTimeoutMs;

    /**
     * 确保所有服务节点全部应答，数据发送成功
     */
    private String acks = "-1";
    private String keySerializer = "org.apache.kafka.common.serialization.StringSerializer";
    private String valueSerializer = "org.apache.kafka.common.serialization.StringSerializer";;
    public Properties toProperties() {
        Properties properties = new Properties();
        try{
            Field[] fields=this.getClass().getDeclaredFields();
            for (Field f : fields) {
                f.setAccessible(true);
                Object val = f.get(this);
                if (!Objects.isNull(val)) {
                    properties.put(StringUtil.convertName(f.getName()),val);
                }
            }
            return properties;
        }catch (Exception e){
            throw new RuntimeException("To get ProducerConf is fail",e);
        }


    }

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public Boolean getEnableIdempotence() {
        return enableIdempotence;
    }

    public void setEnableIdempotence(Boolean enableIdempotence) {
        this.enableIdempotence = enableIdempotence;
    }

    public Integer getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
    }

    public Integer getLingerMs() {
        return lingerMs;
    }

    public void setLingerMs(Integer lingerMs) {
        this.lingerMs = lingerMs;
    }

    public Integer getRequestTimeoutMs() {
        return requestTimeoutMs;
    }

    public void setRequestTimeoutMs(Integer requestTimeoutMs) {
        this.requestTimeoutMs = requestTimeoutMs;
    }

    public String getAcks() {
        return acks;
    }

    public void setAcks(String acks) {
        this.acks = acks;
    }

    public String getKeySerializer() {
        return keySerializer;
    }

    public void setKeySerializer(String keySerializer) {
        this.keySerializer = keySerializer;
    }

    public String getValueSerializer() {
        return valueSerializer;
    }

    public void setValueSerializer(String valueSerializer) {
        this.valueSerializer = valueSerializer;
    }
}
