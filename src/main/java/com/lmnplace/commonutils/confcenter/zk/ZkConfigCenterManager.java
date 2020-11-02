package com.lmnplace.commonutils.confcenter.zk;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lmnplace.commonutils.common.zk.ZkClientSingleton;
import com.lmnplace.commonutils.confcenter.ConfigCenterManager;
import com.lmnplace.commonutils.confcenter.conf.CenterConfig;
import com.lmnplace.commonutils.confcenter.conf.ConfKV;
import com.lmnplace.commonutils.distlock.DistLockHelper;
import com.lmnplace.commonutils.registcenter.mode.ServiceNode;
import com.lmnplace.commonutils.utils.StringUtil;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.IZkStateListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.collections.CollectionUtils;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ZkConfigCenterManager implements ConfigCenterManager {
    private static final Logger log = LoggerFactory.getLogger(ZkConfigCenterManager.class);
    public static final String MANAGER_PROP_KEY = "confcenter.share.manager.address";
    private static final String AICONFCENTER = "/AI_CONFIGCENTER";
    private static final String SHARE = "/share";
    private static final String GLOBAL = AICONFCENTER + "/GLOBAL";
    private static final String SELF = "/self";
    private ServiceNode currrentNode;
    private CenterConfig centerConfig;
    private ConfKV localConf;
    private ConfKV globalConf;
    private ZkClient zkClient;

    private ZkConfigCenterManager(CenterConfig centerConfig, ServiceNode currrentNode, Map<String, String> properties) throws Exception {
        this.centerConfig = centerConfig;
        this.currrentNode = currrentNode;
        if (!centerConfig.isEnableConn()) {
            localConf = ConfKV.build(properties, this, currrentNode);
        } else {
            //初始化分布式锁
            DistLockHelper.init(ZkClientSingleton.instance().getZkConf());
            this.zkClient = ZkClientSingleton.instance().getZkClient();
            //监听zk连接状态
            this.zkClient.subscribeStateChanges(new ZkConfigCenterManager.HAStateListener());
            if (!this.zkClient.exists(AICONFCENTER)) {
                this.zkClient.createPersistent(AICONFCENTER);
            }
            if (!Objects.isNull(properties)) {
                registerConf(currrentNode.getServiceType(), currrentNode.getIp(), currrentNode.getPort(), properties);
            }
        }
    }

    private ZkConfigCenterManager() {
    }

    public static ZkConfigCenterManager buildClusterManager(CenterConfig centerConfig, ServiceNode currrentNode, Map<String, String> properties) throws Exception {
        return new ZkConfigCenterManager(centerConfig, currrentNode, properties);
    }

    @Override
    public ConfKV getGlobalConf() {
        if (!centerConfig.isEnableConn()) {
            throw new RuntimeException("getGlobalConf is not supported,because not enable connect confCenter");
        }
        return ConfKV.build(ConfKV.converConf(zkClient.readData(GLOBAL)), this, currrentNode);
    }

    @Override
    public Boolean updateGlobalConf(Map<String, String> properties) throws Exception {
        if (!centerConfig.isEnableConn()) {
            throw new RuntimeException("updateGlobalConf is not supported,because not enable connect confCenter");
        }
        if (!centerConfig.isEnableManager()) {
            throw new RuntimeException("updateGlobalConf is not supported,because it is not  confCenter manager");
        }
        DistLockHelper.acquire(DistLockHelper.DISKLOCKBASEPATH);
        try {
            String json = zkClient.readData(GLOBAL);
            Map<String, String> prop = ConfKV.converGlobalConf(ConfKV.converConf(json));
            Map<String, String> newProp = ConfKV.converGlobalConf(properties);
            for (Map.Entry<String, String> entry : newProp.entrySet()) {
                prop.put(entry.getKey(), entry.getValue());
            }
            zkClient.writeData(GLOBAL, prop);
        } finally {
            DistLockHelper.release(DistLockHelper.DISKLOCKBASEPATH);
        }
        return true;
    }

    @Override
    public Map<String, ConfKV> getAllShareConf() {
        if (!centerConfig.isEnableConn()) {
            throw new RuntimeException("upateConf is not supported,because not enable connect confCenter");
        }
        Map<String, ConfKV> confs = Maps.newHashMap();
        List<String> services = aiConfCenterServices();
        for(String s: services){
            confs.put(s, getShareConf(s));
        }
        return confs;
    }

    private List<String> aiConfCenterServices() {
        List<String> services = zkClient.getChildren(AICONFCENTER);
        if (CollectionUtils.isNotEmpty(services)) {
            Iterator<String> it = services.iterator();
            while (it.hasNext()) {
                String name = it.next();
                if (GLOBAL.endsWith(name)) {
                    it.remove();
                }
            }
        } else {
            services = Lists.newArrayList();
        }
        return services;
    }
    @Override
    public Boolean registerConf(String serviceType, String host, Integer port, Map<String, String> properties) throws Exception {
        if (!centerConfig.isEnableConn()) {
            this.localConf = ConfKV.build(properties, this, currrentNode);
            return true;
        }
        boolean isExist = zkClient.exists(GLOBAL);
        if (!isExist) {
            DistLockHelper.acquire(DistLockHelper.DISKLOCKBASEPATH);
            try {
                isExist = zkClient.exists(GLOBAL);
                if (!isExist) {
                    zkClient.createPersistent(GLOBAL);
                    Map<String, String> prop = Maps.newHashMap();
                    if (centerConfig.isEnableManager()) {
                        prop = ConfKV.converGlobalConf(properties);
                    }
                    zkClient.writeData(GLOBAL, prop);
                    globalConf = ConfKV.build(prop, this, currrentNode);
                    ListenGlobalShareConf();
                }
            } finally {
                DistLockHelper.release(DistLockHelper.DISKLOCKBASEPATH);
            }
        } else {
            ListenGlobalShareConf();
        }
        Map<String, String> globalProp = null;
        if (isExist) {
            String json = zkClient.readData(GLOBAL);
            globalProp = ConfKV.converConf(json);
        }
        DistLockHelper.acquire(serviceType);
        try {
            String path = String.format("%s/%s", AICONFCENTER, serviceType);
            if (!zkClient.exists(String.format("%s/%s", AICONFCENTER, serviceType))) {
                zkClient.createPersistent(path);
            }
            this.localConf = ConfKV.build(properties, this, currrentNode);
            if(!centerConfig.isForceUseSelfConf()){
                localConf.fromShareUpdateSelfProperties(globalProp);
            }
            Map<String, String> shareConf = null;
            String sharePath = String.format("%s/%s%s", AICONFCENTER, serviceType, SHARE);
            if (!zkClient.exists(sharePath)) {
                zkClient.createPersistent(sharePath, localConf.converShareConf().getKVs());
            } else if ((!Objects.isNull(shareConf = ConfKV.converConf(zkClient.readData(sharePath, true)))) && centerConfig.isStartServiceForceResetShareConf()) {
                //强制重置共享配置信息
                zkClient.writeData(sharePath, localConf.resetShareConf(shareConf));
            } else if (!Objects.isNull(shareConf)) {
                //填充新增共享配置信息
                zkClient.writeData(sharePath, localConf.toFillNewShareProperties(shareConf));
            } else {
                zkClient.writeData(sharePath, localConf.converShareConf());
            }
            String selfPath = String.format("%s/%s%s", AICONFCENTER, serviceType, SELF);
            if (!zkClient.exists(selfPath)) {
                zkClient.createPersistent(selfPath);
            }
            String currentConfPath = String.format("%s/%s_%s", selfPath, currrentNode.getIp(), currrentNode.getPort());
            //配置中心已有配置，则更新本地配置
            Map<String, String> selfProp = Objects.isNull(shareConf) || centerConfig.isForceUseSelfConf() ? localConf.getKVs() : localConf.fromShareUpdateSelfProperties(shareConf);
            if (centerConfig.isEnableManager()) {
                selfProp.put(MANAGER_PROP_KEY, String.format("%s:%s", currrentNode.getIp(), currrentNode.getPort()));
            }
            pushCurrentConf(currentConfPath, selfProp);
            //监听共享配置信息变化
            ListenServiceShareConf(sharePath, currentConfPath);
            //监听self配置信息变化
            ListenSelfConf(currentConfPath);
            return true;
        } finally {
            DistLockHelper.release(serviceType);
        }
    }

    private void ListenSelfConf(String currentConfPath) {
        zkClient.subscribeDataChanges(currentConfPath, new IZkDataListener() {
            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {
                log.info(String.format("the data of %s is changed,data:%s ", dataPath, data));
                if (dataPath.equals(currentConfPath)) {
                    Map<String, String> prop = ConfKV.converConf((String) data);
                    for (Map.Entry<String, String> entry : prop.entrySet()) {
                        localConf.set(entry.getKey(), entry.getValue());
                    }
                }
            }

            @Override
            public void handleDataDeleted(String dataPath) throws Exception {
                log.info(String.format("the data of %s is deleted", dataPath));
            }
        });
    }

    private void ListenServiceShareConf(String sharePath, String currentConfPath) {
        zkClient.subscribeDataChanges(sharePath, new IZkDataListener() {
            @Override
            public void handleDataChange(String dataPath, Object data) {
                log.info(String.format("the data of %s is changed,data:%s ", dataPath, data));
                if (dataPath.equals(sharePath)&&!centerConfig.isForceUseSelfConf()) {
                    Map<String, String> prop = ConfKV.converConf((String) data);
                    localConf.fromShareUpdateSelfProperties(prop);
                    pushCurrentConf(currentConfPath, localConf.getKVs());
                }
            }

            @Override
            public void handleDataDeleted(String dataPath) {
                log.info(String.format("the data of %s is deleted", dataPath));
                if (dataPath.equals(sharePath)) {
                    Iterator it = localConf.getKVs().entrySet().iterator();
                    while (it.hasNext()) {
                        String key = (String) it.next();
                        if (key.contains(ConfKV.SHARECONFFLAG)) {
                            it.remove();
                        }
                    }
                }
            }
        });
    }

    private void ListenGlobalShareConf() {
        ZkConfigCenterManager manager_ = this;
        zkClient.subscribeDataChanges(GLOBAL, new IZkDataListener() {
            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {
                if (GLOBAL.equals(dataPath)) {
                    Map<String, String> prop = ConfKV.converConf((String) data);
                    globalConf = ConfKV.build(ConfKV.converGlobalConf(prop), manager_, currrentNode);
                    if (centerConfig.isEnableManager()) {
                        List<String> services = aiConfCenterServices();
                        if (CollectionUtils.isNotEmpty(services)) {
                            for (String name : services) {
                                DistLockHelper.acquire(name);
                                try {
                                    manager_.updateShareConf(name, manager_.getShareConf(name).fromGlobalShareUpdateProperties(globalConf.getKVs()));
                                } finally {
                                    DistLockHelper.release(name);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void handleDataDeleted(String dataPath) throws Exception {
                if (GLOBAL.equals(dataPath)) {
                    Iterator it = globalConf.getKVs().keySet().iterator();
                    while (it.hasNext()) {
                        it.next();
                        it.remove();
                    }
                }
            }
        });
    }

    @Override
    public Boolean upateSelfConf(String serviceType, String host, Integer port, Map<String, String> properties) {
        if (!centerConfig.isEnableConn()) {
            if (currrentNode.getServiceType().equals(serviceType) && currrentNode.getIp().equals(host) && currrentNode.getPort() == port){
                this.localConf.updateSelfConf(properties);
            }
            return true;
        }
        String selfPath = String.format("%s/%s%s/%s_%s", AICONFCENTER, serviceType, SELF, host, port);
        ConfKV confProperties = getSelfConf(serviceType, host, port);
        zkClient.writeData(selfPath, confProperties.updateSelfConf(properties));
        return true;
    }

    private boolean pushCurrentConf(String currentConfPath, Map<String, String> selfProp) {
        if (!zkClient.exists(currentConfPath)) {
            zkClient.createEphemeral(currentConfPath, selfProp);
        } else {
            zkClient.writeData(currentConfPath, selfProp);
        }
        return true;
    }

    @Override
    public Boolean upateSelfConf(String serviceType, String host, Integer port, String name, String value) throws Exception {
        if (!centerConfig.isEnableConn()) {
            throw new RuntimeException("upateSelfConf is not supported,because not enable connect confCenter");
        }
        if (StringUtil.isBlank(name)) {
            throw new RuntimeException("ConfProperties's key cannot be blank");
        }
        if (StringUtil.isBlank(serviceType)) {
            throw new RuntimeException("serviceType cannot be blank");
        }
        if (centerConfig.isEnableManager() && name.contains(ConfKV.SHARECONFFLAG)) {
            DistLockHelper.acquire(serviceType);
            try {
                String sharePath = String.format("%s/%s%s", AICONFCENTER, serviceType, SHARE);
                String confJson = zkClient.readData(sharePath, true);
                Map<String, String> prop;
                if (!Objects.isNull(confJson) && !Objects.isNull(prop = ConfKV.converConf(confJson))) {
                    prop.put(name, value);
                    zkClient.writeData(sharePath, prop);
                }
            } finally {
                DistLockHelper.release(serviceType);
            }
        } else if (currrentNode.getServiceType().equals(serviceType) && currrentNode.getIp().equals(host) && currrentNode.getPort() == port) {
            String selfPath = String.format("%s/%s%s/%s_%s", AICONFCENTER, serviceType, SELF, host, port);
            localConf.getKVs().put(name, value);
            zkClient.writeData(selfPath, localConf.getKVs());
        } else if (StringUtil.isNotBlank(serviceType) && StringUtil.isNotBlank(host) && !Objects.isNull(port)) {
            String selfPath = String.format("%s/%s%s/%s_%s", AICONFCENTER, serviceType, SELF, host, port);
            String confJson = zkClient.readData(selfPath, true);
            Map<String, String> prop;
            if (!Objects.isNull(confJson) && !Objects.isNull(prop = ConfKV.converConf(confJson))) {
                prop.put(name, value);
                zkClient.writeData(selfPath, prop);
            }
        }
        return true;
    }

    @Override
    public ConfKV getSelfConf(String serviceType, String host, Integer port) {
        if (!centerConfig.isEnableConn()) {
            throw new RuntimeException("getSelfConf is not supported,because not enable connect confCenter");
        }
        String selfPath = String.format("%s/%s%s/%s_%s", AICONFCENTER, serviceType, SELF, host, port);
        String confJson = zkClient.readData(selfPath, true);
        Map<String, String> properties = ConfKV.converConf(confJson);
        return Objects.isNull(properties) ? null : ConfKV.build(properties, this, new ServiceNode(serviceType, host, port));
    }

    @Override
    public ConfKV getShareConf(String serviceType) {
        if (!centerConfig.isEnableConn()) {
            throw new RuntimeException("getShareConf is not supported,because not enable connect confCenter");
        }
        String sharePath = String.format("%s/%s%s", AICONFCENTER, serviceType, SHARE);
        String confJson = zkClient.readData(sharePath, true);
        Map<String, String> properties = ConfKV.converConf(confJson);
        return ConfKV.build(properties, this, new ServiceNode());
    }

    @Override
    public Boolean updateShareConf(String serviceType, Map<String, String> properties) throws Exception {
        if (!centerConfig.isEnableConn()) {
            throw new RuntimeException("updateShareConf is not supported,because not enable connect confCenter");
        }
        if (!centerConfig.isEnableManager()) {
            throw new RuntimeException("updateShareConf is not supported,because it is not  confCenter manager");
        }
        DistLockHelper.acquire(serviceType);
        try {
            String sharePath = String.format("%s/%s%s", AICONFCENTER, serviceType, SHARE);
            boolean isExistPath;
            if ((isExistPath = zkClient.exists(sharePath)) && !Objects.isNull(properties)) {
                Iterator it = properties.keySet().iterator();
                while (it.hasNext()) {
                    String key = (String) it.next();
                    if (!key.contains(ConfKV.SHARECONFFLAG)) {
                        it.remove();
                    }
                }
                zkClient.writeData(sharePath, properties);
                return true;
            } else if (!isExistPath) {
                throw new RuntimeException(String.format("%s is not exist", sharePath));
            } else if (Objects.isNull(properties)) {
                return true;
            } else {
                return false;
            }
        } finally {
            DistLockHelper.release(serviceType);
        }
    }

    @Override
    public ConfKV getCurrentConf() {
        return localConf;
    }

    @Override
    public ConfKV getCurrentShareConf() {
        if (!Objects.isNull(localConf)) {
            return localConf.converShareConf();
        } else {
            return null;
        }
    }

    private void updateSelfFromCurrentShareConf(String serviceType) {
        if (!Objects.isNull(localConf)&&!centerConfig.isForceUseSelfConf()) {
            ConfKV confProperties = getShareConf(serviceType);
            localConf.fromShareUpdateSelfProperties(confProperties.getKVs());
            String currentConfPath = String.format("%s/%s%s/%s_%s", AICONFCENTER, serviceType, SELF, currrentNode.getIp(), currrentNode.getPort());
            pushCurrentConf(currentConfPath, localConf.getKVs());
        }
    }

    // 状态监听
    class HAStateListener implements IZkStateListener {

        @Override
        public void handleStateChanged(Watcher.Event.KeeperState state) throws Exception {

            if (state == Watcher.Event.KeeperState.Disconnected) {
                log.warn("ZkConfigCenterManager Disconnected connection to the zkServer ");
            } else if (state == Watcher.Event.KeeperState.SyncConnected) {
                // 重新连接zk服务器,重新获取一次本服务共享配置
                updateSelfFromCurrentShareConf(currrentNode.getServiceType());
                log.warn("ZkConfigCenterManager reConnection to the zkServer ");
            }
        }

        @Override
        public void handleNewSession() throws Exception {

        }

        @Override
        public void handleSessionEstablishmentError(Throwable error) throws Exception {

        }
    }
}
