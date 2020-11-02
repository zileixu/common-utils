package com.lmnplace.commonutils.monitor.sys.model;


/**
 * Swap內存相关信息
 */
public class SwapModel {
    private long collectTime = System.currentTimeMillis();
    /**
     * 内存总量
     */
    private Long total;

    /**
     * 已用内存
     */
    private Long used;

    /**
     * 剩余内存
     */
    private Long free;

    public long getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(long collectTime) {
        this.collectTime = collectTime;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getUsed() {
        return used;
    }

    public void setUsed(Long used) {
        this.used = used;
    }

    public Long getFree() {
        return free;
    }

    public void setFree(Long free) {
        this.free = free;
    }

    @Override
    public String toString() {
        return "Mem [total=" + total + ", used=" + used + ", free=" + free
                + "]";
    }


}
