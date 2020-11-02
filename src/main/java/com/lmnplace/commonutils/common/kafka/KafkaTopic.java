package com.lmnplace.commonutils.common.kafka;

public interface KafkaTopic {
    /**
     * 数据追踪日志队列主题
     */
    public static final String LOG_TRACKING_DATA_TOPIC="log-tracking-data-topic";
    /**
     * 数据追踪日志队列主题
     */
    public static final String LOG_DISCARD_DATA_TOPIC="log-discard-data-topic";
    /**
     * 资源监控日志队列主题
     */
    public static final String LOG_MONITOR_RESOURCE_TOPIC="log-monitor-resource-topic";
    /**
     * 异常日志队列主题
     */
    public static final String LOG_Ex_TOPIC="log-ex-topic";
    /**
     * 预审服务向算法服务推送数据队列主题
     */
    public static final String RECOG_REQ_TOPIC="recog-req-topic";
    /**
     * 算法服务向预审服务推送数据队列主题
     */
    public static final String RECOG_RESP_TOPIC="recog-resp-topic";


    /**
     * 审核服务向发布代理推送数据队列主题
     */
    public static final String PUSH_REQ_TOPIC="push-req-topic";
    /**
     * 发布代理向审核服务推送数据队列主题
     */
    public static final String PUSH_RESP_TOPIC="push-resp-topic";
}
