package com.lmnplace.commonutils.common.kafka.model;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class KakfaNodeModel implements java.lang.Comparable {
    private Map<String,String> listener_security_protocol_map;
    private List<String> endpoints;
    private int jmx_port;
    private int port;
    private String host;
    private int version;
    private String timestamp;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KakfaNodeModel model = (KakfaNodeModel) o;
        return port == model.port &&
                Objects.equals(host, model.host);
    }

    @Override
    public int hashCode() {
        return Objects.hash(port, host);
    }

    public Map<String, String> getListener_security_protocol_map() {
        return listener_security_protocol_map;
    }

    public void setListener_security_protocol_map(Map<String, String> listener_security_protocol_map) {
        this.listener_security_protocol_map = listener_security_protocol_map;
    }

    public List<String> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(List<String> endpoints) {
        this.endpoints = endpoints;
    }

    public int getJmx_port() {
        return jmx_port;
    }

    public void setJmx_port(int jmx_port) {
        this.jmx_port = jmx_port;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int compareTo(Object o) {
        if(o == null||hashCode() > o.hashCode()){
            return 1;
        }else if(hashCode() == o.hashCode()) {
            return 0;
        }else {
            return -1;
        }
    }
}
