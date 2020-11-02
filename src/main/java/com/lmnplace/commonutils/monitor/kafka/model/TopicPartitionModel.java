package com.lmnplace.commonutils.monitor.kafka.model;

public class TopicPartitionModel {
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
     * 分区日志数量
     */
    private long size;

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

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
