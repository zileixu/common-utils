package com.lmnplace.commonutils.registcenter;

import com.lmnplace.commonutils.registcenter.mode.ServiceNode;

import java.util.List;
import java.util.Map;

public interface RegistCenterManager {
    /**
     * 根据服务类型过去服务节点列表
     *
     * @param serviceType
     * @return
     */
    public List<ServiceNode> getUnmodifiableServices(String serviceType);

    /**
     * 获取所有服务节点列表
     * @return
     */
    public Map<String, List<ServiceNode>> getUnmodifiableAllServices();

    /**
     * 注册服务节点
     * @param data
     */
    public void registerService(ServiceNode data);
    /**
     *  根据服务类型根据均衡机制获取一个节点
     *
     * @param serviceType
     * @return
     */
    public ServiceNode balanceSelectServiceNode(String serviceType);
    /**
     * 根据服务类型及节点id获取服务
     *
     * @param serviceType 服务类型
     * @param serviceId   服务id
     * @return
     */
    public ServiceNode getServiceNode(String serviceType, String serviceId);
    /**
     * 获取当前服务节点
     * @return
     */
    public ServiceNode getCurrentServiceNode();
}
