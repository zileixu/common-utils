package com.lmnplace.commonutils.monitor.kafka.model;

import java.util.List;

public class KafkaMonitorModel {
    private long collectTime = System.currentTimeMillis();
    private List<ConsumerGroupModel> groups;
    private List<TopicModel> topics;

    public List<ConsumerGroupModel> getGroups() {
        return groups;
    }

    public void setGroups(List<ConsumerGroupModel> groups) {
        this.groups = groups;
    }

    public List<TopicModel> getTopics() {
        return topics;
    }

    public void setTopics(List<TopicModel> topics) {
        this.topics = topics;
    }

    public long getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(long collectTime) {
        this.collectTime = collectTime;
    }
}
