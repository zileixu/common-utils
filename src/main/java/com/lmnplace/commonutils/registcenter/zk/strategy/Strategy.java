package com.lmnplace.commonutils.registcenter.zk.strategy;

import com.lmnplace.commonutils.registcenter.mode.ServiceNode;

import java.util.List;

public interface Strategy {

	ServiceNode discovery(List<ServiceNode> nodes);
}