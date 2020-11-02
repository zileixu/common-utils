package com.lmnplace.commonutils.logcenter.model;

public class TrackDataModel {
    /**
     * 数据收集时间
     */
    private long collectTime = System.currentTimeMillis();
    /**
     * 接入uuid
     */
    private String accessUuid;

    /**
     * 违法记录id
     */
    private String recordId;
    /**
     * 原始违法记录id
     */
    private String srcRecordId;
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
     * 业务标识
     */
    private String bizFlag;

    /**
     * 备注，记录数据状态的变化
     */
    private String comment;

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

    public String getBizFlag() {
        return bizFlag;
    }

    public void setBizFlag(String bizFlag) {
        this.bizFlag = bizFlag;
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

    public String getSrcRecordId() {
        return srcRecordId;
    }

    public void setSrcRecordId(String srcRecordId) {
        this.srcRecordId = srcRecordId;
    }
}