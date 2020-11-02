package com.lmnplace.commonutils.monitor.sys.model;

/**
 * 系统文件相关信息
 */
public class DiskModel {
    private long collectTime = System.currentTimeMillis();

    /**
     * 盘符路径
     */
    private String dirName;

    /**
     * 盘符类型
     */
    private String sysTypeName;

    /**
     * 文件类型
     */
    private String typeName;

    /**
     * 总大小，字节
     */
    private Long total;

    /**
     * 剩余大小，字节
     */
    private Long free;

    /**
     * 已经使用量，字节
     */
    private Long used;

    /**
     * 资源的使用率
     */
    private double usedRate;



    public String getDirName() {
        return dirName;
    }

    public void setDirName(String dirName) {
        this.dirName = dirName;
    }

    public String getSysTypeName() {
        return sysTypeName;
    }

    public void setSysTypeName(String sysTypeName) {
        this.sysTypeName = sysTypeName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getFree() {
        return free;
    }

    public void setFree(Long free) {
        this.free = free;
    }

    public Long getUsed() {
        return used;
    }

    public void setUsed(Long used) {
        this.used = used;
    }

    public double getUsedRate() {
        return usedRate;
    }

    public void setUsedRate(double usedRate) {
        this.usedRate = usedRate;
    }

	public long getCollectTime() {
		return collectTime;
	}

	public void setCollectTime(long collectTime) {
		this.collectTime = collectTime;
	}

	@Override
    public String toString() {
        return "SysFile [dirName=" + dirName + ", sysTypeName=" + sysTypeName
                + ", typeName=" + typeName + ", total=" + total + ", free="
                + free + ", used=" + used + ", usedRate=" + usedRate + "]";
    }


}
