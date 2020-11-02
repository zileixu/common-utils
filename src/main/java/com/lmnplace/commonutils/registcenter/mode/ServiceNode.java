package com.lmnplace.commonutils.registcenter.mode;


import com.lmnplace.commonutils.utils.IpUtil;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
public class ServiceNode implements Comparable, Serializable {
	private String id;//服务的唯一id（业务自定义）
	@NotBlank
	private String serviceType;//服务类型(业务自定义)
	@NotBlank
	@Length(min = 7,max = 15)
	private String ip = IpUtil.getHostIp();//主机地址
	private String hostName = IpUtil.getHostName();//主机名
	@Min(0)
	private int port;//主机端口号
	private long createTime;
	@NotNull
	private OpsCmd opsCmd; //运维命令
	/**
	 * Api请求协议必须为http
	 */
	private Map<String,String> apis; //API集合
	public ServiceNode(){}

	public ServiceNode(String serviceType, String ip, int port) {
		this.serviceType = serviceType;
		this.ip = ip;
		this.port = port;
	}

	@Override
	public String toString() {
		return "Service{" +
				"id='" + id + '\'' +
				", host='" + ip + '\'' +
				", port=" + port +
				", serviceType='" + serviceType + '\'' +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ServiceNode service = (ServiceNode) o;
		return  port == service.port &&
				id.equals(service.id) &&
				ip.equals(service.ip) &&
				serviceType.equals(service.serviceType);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, ip, port, serviceType);
	}

	public long getCreateTime() {
		return createTime;
	}

	public ServiceNode setCreateTime(long createTime) {
		this.createTime = createTime;
		return this;
	}

	public Map<String, String> getApis() {
		return apis;
	}

	public ServiceNode setApis(Map<String, String> apis) {
		this.apis = apis;
		return this;
	}

	public String getId() {
		return id;
	}

	public ServiceNode setId(String id) {
		this.id = id;
		return this;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public ServiceNode setPort(int port) {
		this.port = port;
		return this;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public OpsCmd getOpsCmd() {
		return opsCmd;
	}

	public ServiceNode setOpsCmd(@Valid OpsCmd opsCmd) {
		this.opsCmd = opsCmd;
		return this;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
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