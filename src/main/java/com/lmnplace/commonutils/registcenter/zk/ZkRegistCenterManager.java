package com.lmnplace.commonutils.registcenter.zk;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.lmnplace.commonutils.common.constants.CommonConstants;
import com.lmnplace.commonutils.common.zk.ZkClientSingleton;
import com.lmnplace.commonutils.common.zk.ZkConf;
import com.lmnplace.commonutils.exception.ConfParamsValidationException;
import com.lmnplace.commonutils.exception.NoInitException;
import com.lmnplace.commonutils.registcenter.RegistCenterManager;
import com.lmnplace.commonutils.registcenter.conf.RegisterCenterConf;
import com.lmnplace.commonutils.registcenter.mode.ServiceNode;
import com.lmnplace.commonutils.registcenter.zk.strategy.OrderLoopStrategy;
import com.lmnplace.commonutils.registcenter.zk.strategy.Strategy;
import com.lmnplace.commonutils.utils.ValidationUtil;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkStateListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ZkRegistCenterManager implements RegistCenterManager {
    private static ZkRegistCenterManager zkRegistCenterManager;
    private static Lock initLock = new ReentrantLock();
    protected Logger log = LoggerFactory.getLogger(this.getClass());
    private RegisterCenterConf registerCenterConf;
    private ZkClientSingleton zkClientSingleton;//zk客户端
    private ZkClient zkClient;
    private Strategy strategy;// 服务发现策略
    private Lock lock = new ReentrantLock();
    private AtomicInteger updateDataStatus = new AtomicInteger(0);
    private String serviceRootPath;
    private ServiceNode currentNode;
    //已发现的服务资源
    private Map<String, List<ServiceNode>> services = new ConcurrentHashMap<>();
    // 本实例 注册的服务缓存 断线重连后 重新注册
    private Map<String, ServiceRegister> registeredServices = new ConcurrentHashMap<>();

    private ZkRegistCenterManager(RegisterCenterConf conf,ServiceNode serviceNode,ZkClientSingleton zkClientSingleton) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        this.registerCenterConf = conf;
        this.strategy = (Strategy) (StringUtils.isBlank(conf.getBizBalanceStrategy()) ? new OrderLoopStrategy() : Class.forName(conf.getBizBalanceStrategy()).newInstance());
        this.zkClientSingleton = zkClientSingleton;
        this.zkClient =  this.zkClientSingleton.getZkClient();
        this.zkClient.subscribeStateChanges(new HAStateListener());
        this.serviceRootPath= zkClientSingleton.getZkConf().getServiceRootPath();
        this.registerService(serviceNode);
        this.loadServices();
        // 监听child改变
        this.zkClient.subscribeChildChanges(this.serviceRootPath, new HAChildListener());
    }

    public static ZkRegistCenterManager getInstance() throws NoInitException {
        if (Objects.isNull(zkRegistCenterManager)) {
            throw new NoInitException("ClusterManager is not inited");
        }
        return zkRegistCenterManager;
    }

    public static ZkRegistCenterManager buildClusterManager(RegisterCenterConf conf, ServiceNode serviceNode,ZkClientSingleton zkClientSingleton) throws ClassNotFoundException, IllegalAccessException, InstantiationException, ConfParamsValidationException {
        try {
            ValidationUtil.ValidResult validResult = ValidationUtil.validateBean(serviceNode);
            if (validResult.hasErrors()) {
                throw new ConfParamsValidationException(String.format("registerService is fail for %s，%s", JSON.toJSONString(serviceNode), validResult.getErrors()));
            }
            validResult = ValidationUtil.validateBean(serviceNode.getOpsCmd());
            if (validResult.hasErrors()) {
                throw new ConfParamsValidationException(String.format("registerService is fail for %s，%s", JSON.toJSONString(serviceNode), validResult.getErrors()));
            }
            if (Objects.isNull(zkRegistCenterManager)) {
                initLock.lock();
                if (Objects.isNull(zkRegistCenterManager)) {
                    zkRegistCenterManager=new ZkRegistCenterManager(conf,serviceNode,zkClientSingleton);
                    return zkRegistCenterManager ;
                }
            }
            return zkRegistCenterManager;
        } finally {
            initLock.unlock();
        }
    }

    public static void main(String[] args) throws Exception {
        ZkConf conf=new ZkConf();
        conf.setServers("10.10.4.72:2181");
        conf.setClientSerializer("com.seemmo.aicommon.common.serializer.ZkJsonSerializer");
        ZkClientSingleton clientSingleton=ZkClientSingleton.buildZkClient(conf);
        ServiceNode[] dd={new ServiceNode(),new ServiceNode()};
                clientSingleton.getZkClient().writeData("/AI_TRAFFIC","ddd");
        System.out.println((String)clientSingleton.getZkClient().readData("/AI_TRAFFIC"));

      Object data=  clientSingleton.getZkClient().readData("/brokers/ids/0");
        System.out.println(JSON.toJSONString(data));

    }
    /**
     * 根据服务类型过去服务节点列表
     *
     * @param serviceType
     * @return
     */
    public List<ServiceNode> getUnmodifiableServices(String serviceType) {
        List<ServiceNode> serviceNodes = services.get(serviceType);
        return serviceNodes == null ? null : Collections.unmodifiableList(serviceNodes);
    }

    public Map<String, List<ServiceNode>> getUnmodifiableAllServices() {
        if (services == null) {
            return null;
        } else {
            Map<String, List<ServiceNode>> servicesTmp = new ConcurrentHashMap<>();
            for (Map.Entry<String, List<ServiceNode>> entry : services.entrySet()) {
                servicesTmp.put(entry.getKey(), Collections.unmodifiableList((List<? extends ServiceNode>) entry.getValue()));
            }
            return Collections.unmodifiableMap(servicesTmp);
        }
    }

    /**
     * 创建集群配置信息
     *
     * @param path
     * @param data
     * @param mode
     */
    private void createPath(String path, ServiceNode data, CreateMode mode) {
        if (!zkClient.exists(path)) {
            zkClient.create(path, data, mode);
        } else {
            zkClient.writeData(path, data);
        }
        registeredServices.put(path, new ServiceRegister(path, data, mode));
    }

    /**
     * 断线重连 重新注册服务
     */
    private void reRegister() {
        if (!registeredServices.isEmpty()) {
            for (Map.Entry<String, ServiceRegister> entry : registeredServices.entrySet()) {
                ServiceRegister register = entry.getValue();
                createPath(register.getPath(), register.getData(), register.getMode());
            }
        }
    }

    /**
     * 注册服务
     *
     * @param data
     */
    public void registerService(ServiceNode data) {
        String path = null;
        ValidationUtil.ValidResult validResult = ValidationUtil.validateBean(data);
        if (validResult.hasErrors()) {
            throw new RuntimeException(String.format("registerService is fail for %s，%s", JSON.toJSONString(data), validResult.getErrors()));
        }
        validResult = ValidationUtil.validateBean(data.getOpsCmd());
        if (validResult.hasErrors()) {
            throw new RuntimeException(String.format("registerService is fail for %s，%s", JSON.toJSONString(data), validResult.getErrors()));
        }
        try {
            if (!zkClient.exists(serviceRootPath)) {
                createPath(serviceRootPath, null, CreateMode.PERSISTENT);
            }
            if (StringUtils.isBlank(data.getId())) {
                data.setId(String.valueOf(System.currentTimeMillis()));
            }
            path = String.format("%s%s%s%s%s", serviceRootPath,
                    CommonConstants.CommonFlag.RIGHTSLASH.getVal(), data.getServiceType(), CommonConstants.CommonFlag.LOWERBA.getVal()
                    , data.getId());
            this.createPath(path, data.setCreateTime(System.currentTimeMillis()), CreateMode.EPHEMERAL);
            this.loadServices();
        } catch (Exception e) {
            throw new RuntimeException("registerService is fail for " + path, e);
        }
        this.currentNode = data;
    }

    /**
     * 服务发现
     *
     * @param serviceType
     * @return
     */
    public ServiceNode balanceSelectServiceNode(String serviceType) {
        return getServiceNode(serviceType, null);
    }

    /**
     * 服务发现
     *
     * @param serviceType 服务类型
     * @param serviceId   服务id
     * @return
     */
    public ServiceNode getServiceNode(String serviceType, String serviceId) {
        if (services.isEmpty() || services.get(serviceType) == null || services.get(serviceType).isEmpty()) {
            if (zkClient.exists(serviceRootPath)) {
                loadServices();
            }
        }
        if (!services.isEmpty() && services.get(serviceType) != null && !services.get(serviceType).isEmpty()) {
            if (serviceId == null) {
                return strategy.discovery(services.get(serviceType));
            } else {
                List<ServiceNode> set = services.get(serviceType);
                if (!set.isEmpty()) {
                    for (ServiceNode service : set) {
                        if (service.getId().equals(serviceId)) {
                            return service;
                        }
                    }
                }
            }

        }
        return null;
    }

    @Override
    public ServiceNode getCurrentServiceNode() {
        if(Objects.isNull(currentNode)){
            throw new NoInitException("currentNode is not inited");
        }
        return currentNode;
    }

    private void loadServices() {
        int status = updateDataStatus.get();
        try {
            lock.lock();
            if (status < updateDataStatus.get()) {
                return;
            }
            Map<String, Set<ServiceNode>> initServices = new ConcurrentHashMap<>();
            List<String> childs = zkClient.getChildren(serviceRootPath);
            if (childs != null && !childs.isEmpty()) {
                for (String childPath : childs) {
                    //此处添加catch,解决不同服务未使用同一的注册组件导致数据结构不一致的问题
                    try{
                        ServiceNode service = JSON.parseObject(zkClient.readData(serviceRootPath + CommonConstants.CommonFlag.RIGHTSLASH.getVal() + childPath),ServiceNode.class);
                        if(Objects.isNull(service)){
                            continue;
                        }
                        if (!initServices.containsKey(service.getServiceType())) {
                            initServices.put(service.getServiceType(), Sets.newConcurrentHashSet());
                        }
                        initServices.get(service.getServiceType()).add(service);
                    }catch (Exception e){
                        log.error("To load services Childs is fail for childPath:"+childPath);
                    }
                }
                log.info("load services Childs=[{}]", childs);
            }
            Map<String, List<ServiceNode>> servicesTmp = services;
            Map<String, List<ServiceNode>> stringListMap= Maps.newConcurrentMap();
            for(Map.Entry<String, Set<ServiceNode>> entry:initServices.entrySet()){
                stringListMap.put(entry.getKey(), new ArrayList<>(entry.getValue()));
            }
            services = stringListMap;
            servicesTmp.clear();
            updateDataStatus.incrementAndGet();
        } finally {
            lock.unlock();
        }

    }

    // 状态监听
    class HAStateListener implements IZkStateListener {

        @Override
        public void handleStateChanged(KeeperState state) throws Exception {

            if (state == KeeperState.Disconnected) {
                log.warn("Disconnected connection to the zkServer ");
            } else if (state == KeeperState.SyncConnected) {
                // 重新连接zk服务器 重新注册缓存信息
                reRegister();
                log.warn("reConnection to the zkServer  reRegister");
            }

        }

        @Override
        public void handleNewSession() throws Exception {

        }

        @Override
        public void handleSessionEstablishmentError(Throwable error) throws Exception {

        }

    }

    class HAChildListener implements IZkChildListener {

        @Override
        public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
            log.info("listener handleChildChange parentPath=[{}]  Childs=[{}]", parentPath, currentChilds);
            if (parentPath.equals(serviceRootPath)) {
                loadServices();
            }
        }
    }

}