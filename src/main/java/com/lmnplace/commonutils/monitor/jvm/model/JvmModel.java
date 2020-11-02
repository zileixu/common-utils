package com.lmnplace.commonutils.monitor.jvm.model;

public class JvmModel {
    private long collectTime = System.currentTimeMillis();
    private String jdkVersion;
    private long heapSpace;
    private long heapUsed;
    private long noHeapSpace;
    private long noHeapUsed;
    /**
     * 等待垃圾回收的对象数量
     */
    private int  waitGcObjectCount;

    public long getHeapSpace() {
        return heapSpace;
    }

    public void setHeapSpace(long heapSpace) {
        this.heapSpace = heapSpace;
    }

    public long getHeapUsed() {
        return heapUsed;
    }

    public void setHeapUsed(long heapUsed) {
        this.heapUsed = heapUsed;
    }

    public long getNoHeapSpace() {
        return noHeapSpace;
    }

    public void setNoHeapSpace(long noHeapSpace) {
        this.noHeapSpace = noHeapSpace;
    }

    public long getNoHeapUsed() {
        return noHeapUsed;
    }

    public void setNoHeapUsed(long noHeapUsed) {
        this.noHeapUsed = noHeapUsed;
    }

    public int getWaitGcObjectCount() {
        return waitGcObjectCount;
    }

    public void setWaitGcObjectCount(int waitGcObjectCount) {
        this.waitGcObjectCount = waitGcObjectCount;
    }

    public String getJdkVersion() {
        return jdkVersion;
    }

    public void setJdkVersion(String jdkVersion) {
        this.jdkVersion = jdkVersion;
    }

    public long getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(long collectTime) {
        this.collectTime = collectTime;
    }
}
