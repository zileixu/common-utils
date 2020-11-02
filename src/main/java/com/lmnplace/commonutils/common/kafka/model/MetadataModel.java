package com.lmnplace.commonutils.common.kafka.model;


import java.util.List;

public class MetadataModel {
	private String topic;
	private int partitionId;
	private int partitionSize;
	private int leader;
	private List<Integer> isr;
	private List<Integer> replicas;
	private int replicasSize;

	public int getPartitionSize() {
		return partitionSize;
	}

	public void setPartitionSize(int partitionSize) {
		this.partitionSize = partitionSize;
	}

	public int getReplicasSize() {
		return replicasSize;
	}

	public void setReplicasSize(int replicasSize) {
		this.replicasSize = replicasSize;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public int getPartitionId() {
		return partitionId;
	}

	public void setPartitionId(int partitionId) {
		this.partitionId = partitionId;
	}

	public int getLeader() {
		return leader;
	}

	public void setLeader(int leader) {
		this.leader = leader;
	}

	public List<Integer> getIsr() {
		return isr;
	}

	public void setIsr(List<Integer> isr) {
		this.isr = isr;
	}

	public List<Integer> getReplicas() {
		return replicas;
	}

	public void setReplicas(List<Integer> replicas) {
		this.replicas = replicas;
	}

	@Override
	public String toString() {
		return "MetadataInfo{" +
				"topic='" + topic + '\'' +
				", partitionId=" + partitionId +
				", partitionSize=" + partitionSize +
				", leader=" + leader +
				", isr='" + isr + '\'' +
				", replicas='" + replicas + '\'' +
				", replicasSize=" + replicasSize +
				'}';
	}
}
