package com.lmnplace.commonutils.monitor.sys.model;

import java.util.List;

/**
 * CPU相关信息
 */
public class NetModel {
    private long collectTime = System.currentTimeMillis();
    /**
     * 网卡名称
     */
    private String name;
    /**
     * 网卡绑定的ip
     */
    private List<String> ips;
    private Long bytesRecv;
    private Long bytesSent;
    private Long packetsRecv;
    private Long packetsSent;
    /**
     * 网卡支持带宽（即每秒处理多少byte数）
     */
    private Long bytesSpeed;
    private Long bytesUseSpeed;
    /**
     * 带宽使用率
     */
    private double useRate;

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

    public List<String> getIps() {
        return ips;
    }

    public void setIps(List<String> ips) {
        this.ips = ips;
    }

    public Long getBytesRecv() {
        return bytesRecv;
    }

    public void setBytesRecv(Long bytesRecv) {
        this.bytesRecv = bytesRecv;
    }

    public Long getBytesSent() {
        return bytesSent;
    }

    public void setBytesSent(Long bytesSent) {
        this.bytesSent = bytesSent;
    }

    public Long getPacketsRecv() {
        return packetsRecv;
    }

    public void setPacketsRecv(Long packetsRecv) {
        this.packetsRecv = packetsRecv;
    }

    public Long getPacketsSent() {
        return packetsSent;
    }

    public void setPacketsSent(Long packetsSent) {
        this.packetsSent = packetsSent;
    }

    public Long getBytesSpeed() {
        return bytesSpeed;
    }

    public void setBytesSpeed(Long bytesSpeed) {
        this.bytesSpeed = bytesSpeed;
    }

    public Long getBytesUseSpeed() {
        return bytesUseSpeed;
    }

    public void setBytesUseSpeed(Long bytesUseSpeed) {
        this.bytesUseSpeed = bytesUseSpeed;
    }

    public double getUseRate() {
        return useRate;
    }

    public void setUseRate(double useRate) {
        this.useRate = useRate;
    }
}
