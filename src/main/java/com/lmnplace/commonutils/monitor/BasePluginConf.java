package com.lmnplace.commonutils.monitor;

import javax.validation.constraints.Min;

public class BasePluginConf {
    /**
     * 是否启动数据收集，默认false
     */
    private boolean enable=false;
    /**
     * 收集时间间隔，单位毫秒，60s
     */
    @Min(1000)
    private int intervalTime=60000;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public int getIntervalTime() {
        return intervalTime;
    }

    public void setIntervalTime(int intervalTime) {
        this.intervalTime = intervalTime;
    }
}
