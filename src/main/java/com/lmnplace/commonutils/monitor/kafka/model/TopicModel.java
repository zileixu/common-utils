package com.lmnplace.commonutils.monitor.kafka.model;

import java.util.List;

public class TopicModel {
    private Long collectTime = System.currentTimeMillis();
    private String topic;
    private Boolean internal;
    private List<PartitionModel> partitions;
    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Boolean getInternal() {
        return internal;
    }

    public void setInternal(Boolean internal) {
        this.internal = internal;
    }

    public List<PartitionModel> getPartitions() {
        return partitions;
    }

    public void setPartitions(List<PartitionModel> partitions) {
        this.partitions = partitions;
    }

    public Long getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(Long collectTime) {
        this.collectTime = collectTime;
    }
}
