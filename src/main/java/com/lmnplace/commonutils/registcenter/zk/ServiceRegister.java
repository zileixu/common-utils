package com.lmnplace.commonutils.registcenter.zk;

import com.lmnplace.commonutils.registcenter.mode.ServiceNode;
import org.apache.zookeeper.CreateMode;

public class ServiceRegister {

	private String path;
	private ServiceNode data;
	private CreateMode mode;//

	public ServiceRegister() {
	}

	public ServiceRegister(String path, Object data,CreateMode mode) {
		super();
		this.path = path;
		this.data = (ServiceNode) data;
		this.mode = mode;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		return result;
	}

	// 当path 相同是 为同一个对象
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ServiceRegister other = (ServiceRegister) obj;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		return true;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public ServiceNode getData() {
		return data;
	}

	public void setData(ServiceNode data) {
		this.data = data;
	}

	public CreateMode getMode() {
		return mode;
	}

	public void setMode(CreateMode mode) {
		this.mode = mode;
	}

}