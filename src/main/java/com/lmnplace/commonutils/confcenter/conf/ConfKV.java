package com.lmnplace.commonutils.confcenter.conf;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.lmnplace.commonutils.confcenter.ConfigCenterManager;
import com.lmnplace.commonutils.registcenter.mode.ServiceNode;
import com.lmnplace.commonutils.utils.StringUtil;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;

public class ConfKV {
    public static final String SHARECONFFLAG = ".share.";
    public static final String GLOBALCONFFLAG = ".global.share.";
    private static final Logger log = LoggerFactory.getLogger(ConfKV.class);
    private Map<String, String> kvs;
    private ConfigCenterManager manager;
    private ServiceNode node;

    private ConfKV() {
    }

    private ConfKV(Map<String, String> properties, ConfigCenterManager manager, ServiceNode node) {
        this.kvs = properties;
        this.manager = manager;
    }

    public static ConfKV build(Map<String, String> properties, ConfigCenterManager manager, ServiceNode node) {
        return new ConfKV(properties, manager, node);
    }

    public static Map<String, String> converConf(String json) {
        if (!Objects.isNull(json)) {
            try {
                return JSON.parseObject(json, Map.class);
            } catch (Exception e) {
                log.error("json to Properties is fail", e);
                return Maps.newHashMap();
            }
        } else {
            return Maps.newHashMap();
        }

    }

    public static Map<String, String> converGlobalConf(Map<String, String> prop) {
        Map<String, String> properties = Maps.newHashMap();
        if (MapUtils.isNotEmpty(prop)) {
            for (Map.Entry<String, String> entry : prop.entrySet()) {
                if ((entry.getKey()).contains(GLOBALCONFFLAG)) {
                    properties.put(entry.getKey(), entry.getValue());
                }
            }
        }
        return properties;
    }

    public String get(String name, String def) {
        String value = kvs.get(name);
        if (StringUtil.isNotBlank(value)) {
            return value;
        } else {
            return def;
        }
    }

    public double getDouble(String name, double def) {
        String v;
        if (kvs.containsKey(name) && !(Objects.isNull(v = kvs.get(name)))) {
            try {
                return Double.parseDouble(v.trim());
            } catch (Exception e) {
                return def;
            }
        } else {
            return def;
        }
    }

    public long getLong(String name, long def) {
        String v;
        if (kvs.containsKey(name) && !(Objects.isNull(v = kvs.get(name)))) {
            try {
                return Long.parseLong(v.trim());
            } catch (Exception e) {
                return def;
            }
        } else {
            return def;
        }
    }

    public int getInt(String name, int def) {
        String v;
        if (kvs.containsKey(name) && !(Objects.isNull(v = kvs.get(name)))) {
            try {
                return Integer.parseInt(v.trim());
            } catch (Exception e) {
                return def;
            }
        } else {
            return def;
        }
    }

    public <V> boolean getBoolean(String name, boolean def) {
        String v;
        if (kvs.containsKey(name) && !(Objects.isNull(v = kvs.get(name)))) {
            try {
                return Boolean.parseBoolean(v.trim());
            } catch (Exception e) {
                return def;
            }
        } else {
            return def;
        }
    }

    public String set(String name, String value) {
        return set(name, value, false);
    }

    public String set(String name, String value, boolean isPush) {
        if (StringUtil.isBlank(name)) {
            throw new RuntimeException("ConfProperties's key cannot be blank");
        }
        if (isPush) {
            try {
                if (Objects.isNull(node)) {
                    manager.upateSelfConf(null, null, null, name, value);
                } else {
                    manager.upateSelfConf(node.getServiceType(), node.getIp(), node.getPort(), name, value);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return kvs.put(name, value);
    }

    public ConfKV converShareConf() {
        Map<String, String> props = Maps.newHashMap();
        for (Map.Entry<String, String> entry : kvs.entrySet()) {
            if ((entry.getKey()).contains(SHARECONFFLAG)) {
                props.put(entry.getKey(), entry.getValue());
            }
        }
        return ConfKV.build(props, manager, node);
    }

    /**
     * 重置共享配置（仅重置本节点有的共享配置）
     *
     * @param prop
     * @return
     */
    public Map<String, String> resetShareConf(Map<String, String> prop) {
        if (Objects.isNull(prop)) {
            prop = Maps.newHashMap();
        }
        for (Map.Entry<String, String> entry : kvs.entrySet()) {
            if ((entry.getKey()).contains(SHARECONFFLAG)) {
                prop.put(entry.getKey(), entry.getValue());
            }
        }
        return prop;
    }

    /**
     * 将新增的共享配置填充至共享配置集合中
     *
     * @param prop
     * @param <V>
     * @return
     */
    public <V> Map<String, String> toFillNewShareProperties(Map<String, String> prop) {
        if (MapUtils.isEmpty(kvs)) {
            return prop;
        }
        for (Map.Entry<String, String> entry : kvs.entrySet()) {
            if ((entry.getKey()).contains(SHARECONFFLAG) && !prop.containsKey(entry.getKey())) {
                prop.put(entry.getKey(), entry.getValue());
            }
        }
        return prop;
    }

    /**
     * 将来自共享的配置更新至self配置
     *
     * @param prop
     * @return
     */
    public Map<String, String> fromShareUpdateSelfProperties(Map<String, String> prop) {
        if (MapUtils.isEmpty(prop)) {
            return kvs;
        }
        for (Map.Entry<String, String> entry : prop.entrySet()) {
            if ((entry.getKey()).contains(SHARECONFFLAG)) {
                kvs.put(entry.getKey(), entry.getValue());
            }
        }
        return kvs;
    }

    /**
     * 将来自共享的配置更新至self配置
     *
     * @param prop
     * @return
     */
    public Map<String, String> fromGlobalShareUpdateProperties(Map<String, String> prop) {
        if (MapUtils.isEmpty(prop)) {
            return kvs;
        }
        for (Map.Entry<String, String> entry : prop.entrySet()) {
            if ((entry.getKey()).contains(GLOBALCONFFLAG)) {
                kvs.put(entry.getKey(), entry.getValue());
            }
        }
        return kvs;
    }

    /**
     * 将来自共享的配置更新至self配置
     *
     * @param prop
     * @return
     */
    public Map<String, String> updateSelfConf(Map<String, String> prop) {
        if (MapUtils.isEmpty(prop)) {
            return kvs;
        }
        for (Map.Entry<String, String> entry : prop.entrySet()) {
            kvs.put(entry.getKey(), entry.getValue());
        }
        return kvs;
    }

    public Map<String, String> getKVs() {
        return kvs;
    }
}
