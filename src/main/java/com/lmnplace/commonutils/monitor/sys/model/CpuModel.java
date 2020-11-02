package com.lmnplace.commonutils.monitor.sys.model;

/**
 * CPU相关信息
 */
public class CpuModel {
    private long collectTime = System.currentTimeMillis();
    /**
     * 核心数
     */
    private int cpuNum;

    /**
     * CPU系统使用率
     */
    private double sysRate;

    /**
     * CPU用户使用率
     */
    private double userRate;

    /**
     * CPU当前等待率
     */
    private double waitRate;

    /**
     * CPU当前空闲率
     */
    private double freeRate;

    public int getCpuNum() {
        return cpuNum;
    }

    public void setCpuNum(int cpuNum) {
        this.cpuNum = cpuNum;
    }

    public long getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(long collectTime) {
        this.collectTime = collectTime;
    }

    public double getSysRate() {
        return sysRate;
    }

    public void setSysRate(double sysRate) {
        this.sysRate = sysRate;
    }

    public double getUserRate() {
        return userRate;
    }

    public void setUserRate(double userRate) {
        this.userRate = userRate;
    }

    public double getWaitRate() {
        return waitRate;
    }

    public void setWaitRate(double waitRate) {
        this.waitRate = waitRate;
    }

    public double getFreeRate() {
        return freeRate;
    }

    public void setFreeRate(double freeRate) {
        this.freeRate = freeRate;
    }
}
