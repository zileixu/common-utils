package com.lmnplace.commonutils.common.kafka.conf;



import com.lmnplace.commonutils.utils.StringUtil;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Properties;

public class ConsumerConf {
    public static final String DEFGROUPID="DEFCONSUMERGROUPID";
    private String groupId=DEFGROUPID;
    private String bootstrapServers;
    private Boolean enableAutoCommit = false;
    private Integer heartbeatIntervalMs;
    private Integer sessionTimeoutMs;
    private String autoOffsetReset = "latest";
    private String  keyDeserializer = "org.apache.kafka.common.serialization.StringDeserializer";
    private String  valueDeserializer = "org.apache.kafka.common.serialization.StringDeserializer";


    public Properties toProperties() {
        Properties properties=new Properties();
        try {
            Field[] fields=this.getClass().getDeclaredFields();
            for(Field f:fields){
                f.setAccessible(true);
                Object val=f.get(this);
                if(!Objects.isNull(val)){
                    properties.put(StringUtil.convertName(f.getName()),val);
                }
            }
        }catch (Exception e){
            throw new RuntimeException("To get ConsumerConf is fail",e);
        }

        return properties;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public Boolean getEnableAutoCommit() {
        return enableAutoCommit;
    }

    public void setEnableAutoCommit(Boolean enableAutoCommit) {
        this.enableAutoCommit = enableAutoCommit;
    }

    public Integer getHeartbeatIntervalMs() {
        return heartbeatIntervalMs;
    }

    public void setHeartbeatIntervalMs(Integer heartbeatIntervalMs) {
        this.heartbeatIntervalMs = heartbeatIntervalMs;
    }

    public Integer getSessionTimeoutMs() {
        return sessionTimeoutMs;
    }

    public void setSessionTimeoutMs(Integer sessionTimeoutMs) {
        this.sessionTimeoutMs = sessionTimeoutMs;
    }

    public String getAutoOffsetReset() {
        return autoOffsetReset;
    }

    public void setAutoOffsetReset(String autoOffsetReset) {
        this.autoOffsetReset = autoOffsetReset;
    }

    public String getKeyDeserializer() {
        return keyDeserializer;
    }

    public void setKeyDeserializer(String keyDeserializer) {
        this.keyDeserializer = keyDeserializer;
    }

    public String getValueDeserializer() {
        return valueDeserializer;
    }

    public void setValueDeserializer(String valueDeserializer) {
        this.valueDeserializer = valueDeserializer;
    }
}
