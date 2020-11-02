package com.lmnplace.commonutils.monitor.sys.model;

/**
 * 系统文件相关信息
 */
public class DiskIOModel {

    private long collectTime = System.currentTimeMillis();
    /**
     * 盘符名称
     */
    private String name;

    /**
     * 磁盘大小
     */
    private Long size;

    /**
     * 读取次数
     */
    private Long readTimes;

    /**
     * 读取字节数
     */
    private Long readByes;

    /**
     * 写入次数
     */
    private Long writeTimes;
    /**
     * 写入字节数
     */
    private Long writeByes;
    /**
     * 每秒IO字节数
     */
    private Long ioBytesS;

    /**
     * IO占用率
     */
    private Double ioRate;

    public long getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(long collectTime) {
        this.collectTime = collectTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Long getReadTimes() {
        return readTimes;
    }

    public void setReadTimes(Long readTimes) {
        this.readTimes = readTimes;
    }

    public Long getReadByes() {
        return readByes;
    }

    public void setReadByes(Long readByes) {
        this.readByes = readByes;
    }

    public Long getWriteTimes() {
        return writeTimes;
    }

    public void setWriteTimes(Long writeTimes) {
        this.writeTimes = writeTimes;
    }

    public Long getWriteByes() {
        return writeByes;
    }

    public void setWriteByes(Long writeByes) {
        this.writeByes = writeByes;
    }

    public Long getIoBytesS() {
        return ioBytesS;
    }

    public void setIoBytesS(Long ioBytesS) {
        this.ioBytesS = ioBytesS;
    }

    public Double getIoRate() {
        return ioRate;
    }

    public void setIoRate(Double ioRate) {
        this.ioRate = ioRate;
    }
}
