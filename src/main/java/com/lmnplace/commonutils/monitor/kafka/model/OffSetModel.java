package com.lmnplace.commonutils.monitor.kafka.model;

import java.util.Objects;

public class OffSetModel {
    /**
     * 消费组名
     */
    private String groupId;
    /**
     * 主题
     */
    private String topic;
    /**
     * 分区
     */
    private Integer partition;
    /**
     * 消费偏移量
     */
    private Long offset;
    /**
     * 总消息数
     */
    private Long size;
    private String consumerIp;
    private Integer consumerPort;
    /**
     * leder 版本号
     */
    private Integer leaderEpoch;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OffSetModel that = (OffSetModel) o;
        return Objects.equals(groupId, that.groupId) &&
                Objects.equals(topic, that.topic) &&
                Objects.equals(partition, that.partition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, topic, partition);
    }

    public Integer getLeaderEpoch() {
        return leaderEpoch;
    }

    public void setLeaderEpoch(Integer leaderEpoch) {
        this.leaderEpoch = leaderEpoch;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Integer getPartition() {
        return partition;
    }

    public void setPartition(Integer partition) {
        this.partition = partition;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }
}
