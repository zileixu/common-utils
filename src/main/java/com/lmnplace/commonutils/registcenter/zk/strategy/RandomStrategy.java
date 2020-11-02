package com.lmnplace.commonutils.registcenter.zk.strategy;

import com.lmnplace.commonutils.registcenter.mode.ServiceNode;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Random;

public class RandomStrategy implements Strategy {

	private Random rand = new Random();
	
	@Override
	public ServiceNode discovery(List<ServiceNode> nodes) {
		if(CollectionUtils.isEmpty(nodes)){
			return null;
		}
		int index = rand.nextInt(nodes.size());
		return nodes.get(index);
	}

}