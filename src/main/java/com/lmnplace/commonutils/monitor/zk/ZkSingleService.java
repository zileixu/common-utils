package com.lmnplace.commonutils.monitor.zk;

import com.alibaba.fastjson.JSON;
import com.lmnplace.commonutils.common.constants.CommonConstants;
import com.lmnplace.commonutils.common.zk.ZkClientSingleton;
import com.lmnplace.commonutils.common.zk.ZkConf;
import com.lmnplace.commonutils.monitor.zk.model.LeafModel;
import com.lmnplace.commonutils.monitor.zk.model.ZKNodeModel;
import com.lmnplace.commonutils.utils.StringUtil;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.collections.CollectionUtils;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.slf4j.LoggerFactory;

import java.util.*;

public enum ZkSingleService {
    INSTANCE;
    public final static String ZK_ROOT_NODE = "/";
    public final static String ZK_SYSTEM_NODE = "zookeeper"; // ZK internal folder (quota info, etc) - have to stay away from it
    public final static String SOPA_PIPA = "SOPA/PIPA BLACKLISTED VALUE";


    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(ZkSingleService.class);

    private ArrayList<ACL> defaultAcl = ZooDefs.Ids.OPEN_ACL_UNSAFE;

    /**
     * 查询叶子节点
     *
     * @param searchString
     * @return
     * @throws InterruptedException
     * @throws KeeperException
     */
    public Set<LeafModel> searchTree(String searchString) throws InterruptedException, KeeperException {
        Set<LeafModel> searchResult = new TreeSet<>();
        Set<LeafModel> leaves = new TreeSet<>();
        exportTreeInternal(leaves, ZK_ROOT_NODE);
        for (LeafModel leaf : leaves) {
            String leafValue = null;//ServletUtil.INSTANCE.externalizeNodeValue(leaf.getValue());
            if (leaf.getPath().contains(searchString) || leaf.getName().contains(searchString) || leafValue.contains(searchString)) {
                searchResult.add(leaf);
            }
        }
        return searchResult;

    }

    /**
     * 获取所有叶子节点
     *
     * @param path
     * @return
     * @throws InterruptedException
     * @throws KeeperException
     */
    public Set<LeafModel> allLeafs(String path){
        // 1. Collect nodes
        long startTime = System.currentTimeMillis();
        Set<LeafModel> leaves = new TreeSet<>();
        exportTreeInternal(leaves, path);
        long estimatedTime = System.currentTimeMillis() - startTime;
        logger.debug("Elapsed Time in Secs for Export: {} ", estimatedTime / 1000);
        return leaves;
    }

    private void exportTreeInternal(Set<LeafModel> entries, String path){
        // 1. List leaves
        entries.addAll(listLeaves(path));
        // 2. Process folders
        for (String folder : this.listFolders(path)) {
            exportTreeInternal(entries, this.getNodePath(path, folder));
        }
    }

    /**
     * 获取节点信息包括叶节点及目录节点
     * @param path
     * @return
     * @throws KeeperException
     * @throws InterruptedException
     */
    public ZKNodeModel node(String path) {
        List<String> folders = new ArrayList<>();
        List<LeafModel> leaves = new ArrayList<>();
        ZkClient zk = ZkClientSingleton.instance().getZkClient();
        List<String> children = ZkClientSingleton.instance().getZkClient().getChildren(path);
        if (CollectionUtils.isNotEmpty(children)) {
            for (String child : children) {
                List<String> subChildren = zk.getChildren(getNodePath(path, child));
                if (CollectionUtils.isNotEmpty(subChildren)) {
                    folders.add(child);
                } else {
                    String childPath = getNodePath(path, child);
                    leaves.add(this.getNodeValue(path, childPath, child));
                }
            }
        }
        Collections.sort(folders);
        Collections.sort(leaves, new Comparator<LeafModel>() {
            @Override
            public int compare(LeafModel o1, LeafModel o2) {
                if(Objects.isNull(o1)||Objects.isNull(o2)){
                    return -1;
                }
                return StringUtil.compare(o1.getName(),o2.getName());
            }
        });

        ZKNodeModel zkNode = new ZKNodeModel();
        zkNode.setLeafs(leaves);
        zkNode.setNoleafs(folders);
        return zkNode;
    }

    /**
     *  获取目录下的所有一级叶节点
     * @param path
     * @return
     */
    public List<LeafModel> listLeaves(String path) {
        List<LeafModel> leaves = new ArrayList<>();
        ZkClient zk = ZkClientSingleton.instance().getZkClient();
        List<String> children = zk.getChildren(path);
        if (CollectionUtils.isNotEmpty(children)) {
            for (String child : children) {
                String childPath = getNodePath(path, child);
                List<String> subChildren = zk.getChildren(childPath);
                boolean isFolder = CollectionUtils.isNotEmpty(subChildren);
                if (!isFolder) {
                    leaves.add(this.getNodeValue(path, childPath, child));
                }
            }
        }
        Collections.sort(leaves, new Comparator<LeafModel>() {
            @Override
            public int compare(LeafModel o1, LeafModel o2) {
                if(Objects.isNull(o1)||Objects.isNull(o2)){
                    return -1;
                }
                return StringUtil.compare(o1.getName(),o2.getName());
            }
        });

        return leaves;
    }

    /**
     *  获取目录下的所有一级目录节点
     * @param path
     * @return
     */
    public List<String> listFolders(String path) {
        List<String> folders = new ArrayList<>();
        ZkClient zk = ZkClientSingleton.instance().getZkClient();
        List<String> children = zk.getChildren(path);
        if (children != null) {
            for (String child : children) {
                List<String> subChildren = zk.getChildren(getNodePath(path, child));
                if (CollectionUtils.isNotEmpty(subChildren)) {
                    folders.add(child);
                }
            }
        }

        Collections.sort(folders);
        return folders;
    }

    private String getNodePath(String path, String name) {
        return String.format("%s%s%s", path, CommonConstants.CommonFlag.RIGHTSLASH.getVal().equals(path)
                ? "" : CommonConstants.CommonFlag.RIGHTSLASH.getVal(), name);
    }

    private LeafModel getNodeValue(String path, String childPath, String child) {
        ZkClient zk = ZkClientSingleton.instance().getZkClient();
        try {
            logger.debug("Lookup: path=%s,childPath=%s,child=%s", path, childPath, child);
            String value = zk.readData(childPath);
            if (checkIfPwdField(child)) {
                return (new LeafModel(path, child, SOPA_PIPA));
            } else {
                return (new LeafModel(path, child, value));
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
        return null;
    }

    public Boolean checkIfPwdField(String property) {
        if (property.contains("PWD") || property.contains("pwd") || property.contains("PASSWORD") || property.contains("password") || property.contains("PASSWD") || property.contains("passwd")) {
            return true;
        } else {
            return false;
        }
    }

    public static void main(String[] args) throws Exception {
        ZkConf zkConf = new ZkConf();
        zkConf.setServers("10.10.4.49:2181");
        ZkClientSingleton.buildZkClient(zkConf);
        System.out.println(JSON.toJSONString(ZkSingleService.INSTANCE.listLeaves("/")));
        ;
    }
}