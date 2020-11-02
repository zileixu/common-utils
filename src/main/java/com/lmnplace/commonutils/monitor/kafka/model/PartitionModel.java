package com.lmnplace.commonutils.monitor.kafka.model;

import java.util.List;

public class PartitionModel {
    private int partition;
    /**
     * 当前分区日志量
     */
    private Long size;
    private BrokerModel leader;
    private List<BrokerModel> replicas;
    private List<BrokerModel> isr;

    public int getPartition() {
        return partition;
    }

    public BrokerModel getLeader() {
        return leader;
    }

    public List<BrokerModel> getReplicas() {
        return replicas;
    }

    public List<BrokerModel> getIsr() {
        return isr;
    }

    public void setPartition(int partition) {
        this.partition = partition;
    }

    public void setLeader(BrokerModel leader) {
        this.leader = leader;
    }

    public void setReplicas(List<BrokerModel> replicas) {
        this.replicas = replicas;
    }

    public void setIsr(List<BrokerModel> isr) {
        this.isr = isr;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }
}
