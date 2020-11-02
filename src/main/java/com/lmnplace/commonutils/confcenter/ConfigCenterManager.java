package com.lmnplace.commonutils.confcenter;

import com.lmnplace.commonutils.confcenter.conf.ConfKV;

import java.util.Map;

public interface ConfigCenterManager {
    /**
     * 获取全局配置
     * @return
     */
    ConfKV getGlobalConf();

    /**
     * 更新全局配置
     *
     * @return
     */
    Boolean updateGlobalConf(Map<String, String> kvs) throws Exception;

    /**
     * 获取所有服务共享配置
     *
     * @return
     */
    Map<String, ConfKV> getAllShareConf();

    /**
     * 注册配置
     * @param serviceType
     * @param host
     * @param port
     * @param kvs
     * @return
     */
    Boolean registerConf(String serviceType, String host, Integer port, Map<String, String> kvs) throws Exception;

    /**
     * 更新配置
     *
     * @param serviceType
     * @param host
     * @param port
     * @param kvs
     * @return
     * @throws Exception
     */
    Boolean upateSelfConf(String serviceType, String host, Integer port, Map<String, String> kvs) throws Exception;
    /**
     * 更新配置
     * @param serviceType
     * @param host
     * @param port
     * @param name
     * @param value
     * @return
     * @throws Exception
     */
    Boolean upateSelfConf(String serviceType, String host, Integer port, String name, String value) throws Exception;
    /**
     * 获取节点配置
     * @param serviceType
     * @param host
     * @param port
     * @return
     */
    ConfKV getSelfConf(String serviceType, String host, Integer port);

    /**
     * 获取指定服务的共享配置
     * @param serviceType
     * @return
     */
    ConfKV getShareConf(String serviceType);

    /**
     * 更新服务的共享配置
     * @param serviceType
     * @param kvs
     * @return
     */
    Boolean updateShareConf(String serviceType, Map<String, String> kvs) throws Exception;

    /**
     * 获取当前节点配置
     * @return
     */
    ConfKV getCurrentConf();
    /**
     * 获取当前服务共享配置
     * @return
     */
    ConfKV getCurrentShareConf();
}
