package com.lmnplace.commonutils.monitor.kafka.model;

import java.util.List;

public class MemberModel {
    private String consumerId;
    private String clientId;
    private String host;
    private List<TopicPartitionModel> topicPartitions;

    public String getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public List<TopicPartitionModel> getTopicPartitions() {
        return topicPartitions;
    }

    public void setTopicPartitions(List<TopicPartitionModel> topicPartitions) {
        this.topicPartitions = topicPartitions;
    }
}
