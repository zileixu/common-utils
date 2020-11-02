package com.lmnplace.commonutils.logcenter.model;

public class DiscardDataModel {
    /**
     * 数据收集时间
     */
    private long collectTime = System.currentTimeMillis();
    /**
     * 违法记录id
     */
    private String recordId;

    /**
     * 厂商编码
     */
    private String manufacturerCode;

    /**
     * 原始设备违法编码
     */
    private String deviceCode;

    /**
     * 原始违法编码
     */
    private String illegalCode;

    /**
     * 原始车牌号
     */
    private String plateNumber;

    /**
     * 违法时间
     */
    private Long snapshotTime;

    /**
     * 数据屏蔽原因
     */
    private String discardReason;

    /**
     * 备注
     */
    private String comment;
    /**
     * 规则名称
     */
    private String ruleName;
    /**
     * 数据接入唯一标识
     */
    private String accessUuid;

    public String getAccessUuid() {
        return accessUuid;
    }

    public void setAccessUuid(String accessUuid) {
        this.accessUuid = accessUuid;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getManufacturerCode() {
        return manufacturerCode;
    }

    public void setManufacturerCode(String manufacturerCode) {
        this.manufacturerCode = manufacturerCode;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public String getIllegalCode() {
        return illegalCode;
    }

    public void setIllegalCode(String illegalCode) {
        this.illegalCode = illegalCode;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public Long getSnapshotTime() {
        return snapshotTime;
    }

    public void setSnapshotTime(Long snapshotTime) {
        this.snapshotTime = snapshotTime;
    }

    public String getDiscardReason() {
        return discardReason;
    }

    public void setDiscardReason(String discardReason) {
        this.discardReason = discardReason;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(long collectTime) {
        this.collectTime = collectTime;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }
}