package com.lmnplace.commonutils.registcenter.zk.strategy;

import com.google.common.collect.Maps;
import com.lmnplace.commonutils.registcenter.mode.ServiceNode;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class OrderLoopStrategy implements Strategy {

	private Map<String,AtomicInteger> indexs = Maps.newConcurrentMap();
	@Override
	public ServiceNode discovery(List<ServiceNode> nodes) {
		if(CollectionUtils.isEmpty(nodes)){
			return  null;
		}
		ServiceNode serviceNode=nodes.get(0);
		AtomicInteger index=indexs.get(serviceNode.getServiceType());
		if(Objects.isNull(index)||index.get() == Integer.MAX_VALUE){
			index=new AtomicInteger(0);
			indexs.put(serviceNode.getServiceType(),index);
		}else {
			index.incrementAndGet();
		}
		return nodes.get(index.get()%nodes.size());
	}
}