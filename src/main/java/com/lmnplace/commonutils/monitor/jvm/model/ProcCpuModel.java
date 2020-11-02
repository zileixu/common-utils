package com.lmnplace.commonutils.monitor.jvm.model;

public class ProcCpuModel {
    private long collectTime = System.currentTimeMillis();
    /**
     * 进程id
     */
    private long pid;
    /**
     * 总体使用率,每核cpu最高使用为1，即为100%
     */
    private double cpuTotalRate;
    /**
     * 用户使用率,每核cpu最高使用为1，即为100%
     */
    private double cpuUserRate;
    /**
     * 系统使用率,每核cpu最高使用为1，即为100%
     */
    private double cpuSysRate;

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public double getCpuTotalRate() {
        return cpuTotalRate;
    }

    public void setCpuTotalRate(double cpuTotalRate) {
        this.cpuTotalRate = cpuTotalRate;
    }

    public double getCpuUserRate() {
        return cpuUserRate;
    }

    public void setCpuUserRate(double cpuUserRate) {
        this.cpuUserRate = cpuUserRate;
    }

    public double getCpuSysRate() {
        return cpuSysRate;
    }

    public void setCpuSysRate(double cpuSysRate) {
        this.cpuSysRate = cpuSysRate;
    }

    public long getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(long collectTime) {
        this.collectTime = collectTime;
    }
}
