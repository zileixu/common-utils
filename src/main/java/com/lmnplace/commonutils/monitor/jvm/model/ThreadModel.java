package com.lmnplace.commonutils.monitor.jvm.model;


public class ThreadModel {
    /**
     * 数据收集时间
     */
    private long collectTime = System.currentTimeMillis();
    /**
     * 总线程数
     */
    private long totalCount;
	/**
	 * 峰值线程数
	 */
	private int peekCount;
    /**
     * 守护线程数
     */
    private int daemonCount;
    /**
     * 新建线程
     */
    private int newCount;
    /**
     * 运行中的线程
     */
    private int runnableCount;
	/**
	 * Thread state for a thread blocked waiting for a monitor lock.
	 */
    private int blockedCount;

    /**
     * 这个状态下是指线程拥有了某个锁之后, 调用了他的wait方法, 等待的线程数
     */
    private int waitingCount;
    /**
     * 这个状态就是有限的(时间限制)的WAITING, 一般出现在调用wait(long), join(long)等情况下, 另外一个线程sleep后
     * 休眠的线程数
     */

    private int timedWaitingCount;

    /**
     * Thread state for a terminated thread.
     * The thread has completed execution.
     */
    private int terminatedCount;

	public long getCollectTime() {
		return collectTime;
	}

	public void setCollectTime(long collectTime) {
		this.collectTime = collectTime;
	}

	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}

	public int getPeekCount() {
		return peekCount;
	}

	public void setPeekCount(int peekCount) {
		this.peekCount = peekCount;
	}

	public int getDaemonCount() {
		return daemonCount;
	}

	public void setDaemonCount(int daemonCount) {
		this.daemonCount = daemonCount;
	}

	public int getNewCount() {
		return newCount;
	}

	public void setNewCount(int newCount) {
		this.newCount = newCount;
	}

	public int getRunnableCount() {
		return runnableCount;
	}

	public void setRunnableCount(int runnableCount) {
		this.runnableCount = runnableCount;
	}

	public int getBlockedCount() {
		return blockedCount;
	}

	public void setBlockedCount(int blockedCount) {
		this.blockedCount = blockedCount;
	}

	public int getWaitingCount() {
		return waitingCount;
	}

	public void setWaitingCount(int waitingCount) {
		this.waitingCount = waitingCount;
	}

	public int getTimedWaitingCount() {
		return timedWaitingCount;
	}

	public void setTimedWaitingCount(int timedWaitingCount) {
		this.timedWaitingCount = timedWaitingCount;
	}

	public int getTerminatedCount() {
		return terminatedCount;
	}

	public void setTerminatedCount(int terminatedCount) {
		this.terminatedCount = terminatedCount;
	}
}
