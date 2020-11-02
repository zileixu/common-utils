package com.lmnplace.commonutils.logcenter.model;

public class ExLogModel {
    /**
     * 数据收集时间
     */
    private long collectTime = System.currentTimeMillis();
    /**
     * 日志级别：WARN、EROR、FATAL、OFF
     */
    private String logLevel;

    /**
     * 日志业务标识
     */
    private String bizFlag;

    /**
     * 日志信息
     */
    private String errorMsg;
    /**
     * @return id
     */

    /**
     * 获取日志级别：WARN、EROR、FATAL、OFF
     *
     * @return log_level - 日志级别：WARN、EROR、FATAL、OFF
     */
    public String getLogLevel() {
        return logLevel;
    }

    /**
     * 设置日志级别：WARN、EROR、FATAL、OFF
     *
     * @param logLevel 日志级别：WARN、EROR、FATAL、OFF
     */
    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    /**
     * 获取日志业务标识
     *
     * @return biz_flag - 日志业务标识
     */
    public String getBizFlag() {
        return bizFlag;
    }

    /**
     * 设置日志业务标识
     *
     * @param bizFlag 日志业务标识
     */
    public void setBizFlag(String bizFlag) {
        this.bizFlag = bizFlag;
    }

    /**
     * 获取日志信息
     *
     * @return error_msg - 日志信息
     */
    public String getErrorMsg() {
        return errorMsg;
    }

    /**
     * 设置日志信息
     *
     * @param errorMsg 日志信息
     */
    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public long getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(long collectTime) {
        this.collectTime = collectTime;
    }
}