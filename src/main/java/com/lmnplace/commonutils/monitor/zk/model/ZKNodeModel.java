package com.lmnplace.commonutils.monitor.zk.model;

import java.util.ArrayList;
import java.util.List;

public class ZKNodeModel {

    List<String> noleafs;
    List<LeafModel> leafs;

    public ZKNodeModel() {
        noleafs = new ArrayList<>();
        leafs = new ArrayList<>();
    }

    public List<String> getNoleafs() {
        return noleafs;
    }

    public void setNoleafs(List<String> noleafs) {
        this.noleafs = noleafs;
    }

    public List<LeafModel> getLeafs() {
        return leafs;
    }

    public void setLeafs(List<LeafModel> leafs) {
        this.leafs = leafs;
    }
}