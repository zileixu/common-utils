package com.lmnplace.commonutils.monitor.jvm.model;

public class GcModel {
    private long collectTime = System.currentTimeMillis();

    /**
     * Survivor空间的大小。单位B。
     */
    private long ss;
    /**
     * Survivor已使用大小。单位B。
     */
    private long su;
    /**
     * 最近一次gc使用变化
     */
    private long lastGcSu;
    /**
     * Eden空间的大小。单位B。
     */
    private long es;
    /**
     * Eden已用空间的大小。单位B。
     */
    private long eu;
    /**
     * 最近一次gc使用变化
     */
    private long lastGcEu;
    /**
     * 老年代空间的大小。单位B。
     */
    private long os;
    /**
     * 老年代已用空间的大小。单位B。
     */
    private long ou;
    /**
     * 最近一次gc使用变化
     */
    private long lastGcOu;
    /**
     * 元空间的大小（Metaspace）
     */
    private long ms;
    /**
     * 元空间已使用大小（B）
     */
    private long mu;

    /**
     * Compressed Class Space 属于Metaspace空间的一部分
     */
    private long mccss;
    /**
     * Compressed Class Space 的使用空间
     */
    private long mccsu;
    /**
     * 最近一次gc使用变化
     */
    private long lastGcMu;
    /**
     * 压缩类空间大小（compressed class space）
     */
    private long ccss;
    /**
     * 压缩类空间已使用大小（byte）
     */
    private long ccsu;
    /**
     * 最近一次gc使用变化
     */
    private long lastGcCccsu;
    /**
     * 新生代gc总次数
     */
    private long totalYgc;
    /**
     * 新生代gc总耗时（毫秒）
     */
    private long totalYgct;
    /**
     * 最后一次ygc耗时
     */
    private long lastGcYgct;

    /**
     * Full gc总次数
     */
    private long totalFgc;
    /**
     * Full gc总耗时（毫秒）
     */
    private long totalFgct;
    /**
     * 最后一次fgc耗时
     */
    private long lastGcFgct;


    public long getMccsu() {
        return mccsu;
    }

    public void setMccsu(long mccsu) {
        this.mccsu = mccsu;
    }

    public long getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(long collectTime) {
        this.collectTime = collectTime;
    }


    public long getSu() {
        return su;
    }

    public void setSu(long su) {
        this.su = su;
    }


    public long getEu() {
        return eu;
    }

    public void setEu(long eu) {
        this.eu = eu;
    }


    public void setOu(long ou) {
        this.ou = ou;
    }

    public long getLastGcOu() {
        return lastGcOu;
    }

    public void setLastGcOu(long lastGcOu) {
        this.lastGcOu = lastGcOu;
    }

    public long getLastGcMu() {
        return lastGcMu;
    }

    public void setLastGcMu(long lastGcMu) {
        this.lastGcMu = lastGcMu;
    }


    public long getMu() {
        return mu;
    }

    public void setMu(long mu) {
        this.mu = mu;
    }


    public long getCcsu() {
        return ccsu;
    }

    public void setCcsu(long ccsu) {
        this.ccsu = ccsu;
    }

    public long getLastGcSu() {
        return lastGcSu;
    }

    public void setLastGcSu(long lastGcSu) {
        this.lastGcSu = lastGcSu;
    }

    public long getLastGcEu() {
        return lastGcEu;
    }

    public void setLastGcEu(long lastGcEu) {
        this.lastGcEu = lastGcEu;
    }

    public long getOu() {
        return ou;
    }

	public long getLastGcCccsu() {
		return lastGcCccsu;
	}

	public void setLastGcCccsu(long lastGcCccsu) {
		this.lastGcCccsu = lastGcCccsu;
	}

	public long getTotalYgc() {
        return totalYgc;
    }

    public void setTotalYgc(long totalYgc) {
        this.totalYgc = totalYgc;
    }

    public long getTotalYgct() {
        return totalYgct;
    }

    public void setTotalYgct(long totalYgct) {
        this.totalYgct = totalYgct;
    }

    public long getLastGcYgct() {
        return lastGcYgct;
    }

    public void setLastGcYgct(long lastGcYgct) {
        this.lastGcYgct = lastGcYgct;
    }

    public long getTotalFgc() {
        return totalFgc;
    }

    public void setTotalFgc(long totalFgc) {
        this.totalFgc = totalFgc;
    }

    public long getTotalFgct() {
        return totalFgct;
    }

    public void setTotalFgct(long totalFgct) {
        this.totalFgct = totalFgct;
    }

    public long getLastGcFgct() {
        return lastGcFgct;
    }

    public void setLastGcFgct(long lastGcFgct) {
        this.lastGcFgct = lastGcFgct;
    }

	public long getSs() {
		return ss;
	}

	public void setSs(long ss) {
		this.ss = ss;
	}

	public long getEs() {
		return es;
	}

	public void setEs(long es) {
		this.es = es;
	}

	public long getOs() {
		return os;
	}

	public void setOs(long os) {
		this.os = os;
	}

	public long getMs() {
		return ms;
	}

	public void setMs(long ms) {
		this.ms = ms;
	}

	public long getMccss() {
		return mccss;
	}

	public void setMccss(long mccss) {
		this.mccss = mccss;
	}

	public long getCcss() {
		return ccss;
	}

	public void setCcss(long ccss) {
		this.ccss = ccss;
	}
}
