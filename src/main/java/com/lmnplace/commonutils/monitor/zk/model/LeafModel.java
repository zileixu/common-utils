package com.lmnplace.commonutils.monitor.zk.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class LeafModel implements Comparable<LeafModel> {

    private final static Logger logger = LoggerFactory.getLogger(LeafModel.class);
    private String path;
    private String name;
    private String value;

    public LeafModel(String path, String name, String value) {
        super();
        this.path = path;
        this.name = name;
        this.value=value;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static Logger getLogger() {
        return logger;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int compareTo(LeafModel o) {
        if(Objects.isNull(o)){
            return -1;
        }
        return (this.path + this.name).compareTo((o.path + o.name));
    }
}