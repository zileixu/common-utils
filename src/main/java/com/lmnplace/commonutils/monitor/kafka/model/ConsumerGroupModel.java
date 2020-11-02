package com.lmnplace.commonutils.monitor.kafka.model;

import java.util.List;

public class ConsumerGroupModel {
    private Long collectTime = System.currentTimeMillis();
    private String groupId;
    private List<MemberModel> memebers;
    private String state;
    private BrokerModel coordinator;
    /**
     * consumer partition 分配策略
     */
    private String partitionAssignor;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public List<MemberModel> getMemebers() {
        return memebers;
    }

    public void setMemebers(List<MemberModel> memebers) {
        this.memebers = memebers;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public BrokerModel getCoordinator() {
        return coordinator;
    }

    public void setCoordinator(BrokerModel coordinator) {
        this.coordinator = coordinator;
    }

    public String getPartitionAssignor() {
        return partitionAssignor;
    }

    public void setPartitionAssignor(String partitionAssignor) {
        this.partitionAssignor = partitionAssignor;
    }

    public Long getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(Long collectTime) {
        this.collectTime = collectTime;
    }
}
