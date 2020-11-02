package com.lmnplace.commonutils.common.kafka;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lmnplace.commonutils.common.constants.CommonConstants;
import com.lmnplace.commonutils.common.kafka.conf.ConsumerConf;
import com.lmnplace.commonutils.common.kafka.conf.ProducerConf;
import com.lmnplace.commonutils.common.kafka.conf.kafkaConf;
import com.lmnplace.commonutils.common.kafka.model.KakfaNodeModel;
import com.lmnplace.commonutils.common.kafka.model.MetadataModel;
import com.lmnplace.commonutils.common.zk.ZkClientSingleton;
import com.lmnplace.commonutils.common.zk.ZkConf;
import com.lmnplace.commonutils.monitor.kafka.model.OffSetModel;
import com.lmnplace.commonutils.registcenter.RegistCenterManagerBuilder;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ConsumerGroupDescription;
import org.apache.kafka.clients.admin.ConsumerGroupListing;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class KafkaClientSingleton {
    private static final Logger log = LoggerFactory.getLogger(ProducerConf.class);
    private static KafkaClientSingleton singleton = new KafkaClientSingleton();;
    private static final String BROKERSPARENTPATH = "/brokers/ids";
    private static final String kafkaTopicParentPath = "/brokers/topics";
    private static final String PARTITIONSTATEPath = "/brokers/topics/%s/partitions/%s/state";
    private static final String kafkaPartitionsParenPath = kafkaTopicParentPath + "/%s/partitions";
    private  Set<KakfaNodeModel> KafkaNodes = new ConcurrentSkipListSet();
    private  ReentrantLock loadNodelock = new ReentrantLock();
    private kafkaConf conf;

    public static kafkaConf getImmutableConf(){
        kafkaConf newConf=new kafkaConf();
        if(Objects.isNull(singleton.conf)){
            throw new RuntimeException("Kafka client was not inited");
        }
        BeanUtils.copyProperties(singleton.conf,newConf);
        return newConf;
    }
    private KafkaClientSingleton() {
    }
    public static KafkaClientSingleton instance(kafkaConf conf){
        if(Objects.isNull(singleton.conf)){
            synchronized (BROKERSPARENTPATH) {
                if(Objects.isNull(singleton.conf)){
                    singleton.conf=conf;
                }
            }
        }
        return singleton;
    }

    private KafkaConsumer kafkaConsumerInstance(ConsumerConf conf) {
        Deserializer keyDeserializer = null;
        Deserializer valueDeserializer = null;
        if (Objects.isNull(conf)) {
            conf = new ConsumerConf();
        }
        if (StringUtils.isNotBlank(conf.getKeyDeserializer())) {
            try {
                keyDeserializer = (Deserializer) Class.forName(conf.getKeyDeserializer()).newInstance();
            } catch (Exception e) {
                log.error("To instance kafkaConsumer's keyDeserializer is fail,use default", e);
            }
        }
        if (StringUtils.isNotBlank(conf.getValueDeserializer())) {
            try {
                valueDeserializer = (Deserializer) Class.forName(conf.getValueDeserializer()).newInstance();
            } catch (Exception e) {
                log.error("To instance kafkaConsumer's valueDeserializer is fail,use default", e);
            }
        }
        if (StringUtils.isBlank(conf.getBootstrapServers())) {
            conf.setBootstrapServers(getBootstrapServers());
        }
        return new KafkaConsumer(conf.toProperties(), keyDeserializer, valueDeserializer);
    }
    private  String getBootstrapServers(){
        if(CollectionUtils.isEmpty(KafkaNodes)){
            synchronized (KafkaNodes){
                if(CollectionUtils.isEmpty(KafkaNodes)){
                    ZkClient zkClient = ZkClientSingleton.instance().getZkClient();
                    loadKafkaNodes(zkClient);
                    zkClient.subscribeChildChanges(BROKERSPARENTPATH, new KafkaNodesListener(zkClient));
                }
            }
        }
        StringBuilder nodesAdress = new StringBuilder();
        int size=KafkaNodes.size();
        int tmp=1;
        for (KakfaNodeModel node : KafkaNodes) {
            nodesAdress.append(node.getHost());
            nodesAdress.append(CommonConstants.CommonFlag.MH.getVal());
            nodesAdress.append(node.getPort());
            if(tmp<size){
                nodesAdress.append(CommonConstants.CommonFlag.FH.getVal());
            }
            tmp++;
        }
        return  nodesAdress.toString();
    }

     class KafkaNodesListener implements IZkChildListener {
        private ZkClient client;

        public KafkaNodesListener(ZkClient client) {
            this.client = client;
        }

        @Override
        public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
            if (BROKERSPARENTPATH.equals(parentPath)) {
                loadKafkaNodes(client);
            }
        }
    }

    private  void loadKafkaNodes(ZkClient client) {
        try {
            loadNodelock.lock();
            List<String> cnodes = client.getChildren(BROKERSPARENTPATH);
            if (CollectionUtils.isNotEmpty(cnodes)) {
                Set<KakfaNodeModel> newKafkaNodes = new ConcurrentSkipListSet();
                for (String n : cnodes) {
                    KakfaNodeModel model = JSON.parseObject(client.readData(String.format("%s%s%s"
                            , BROKERSPARENTPATH, CommonConstants.CommonFlag.RIGHTSLASH.getVal(), n)), KakfaNodeModel.class);
                    newKafkaNodes.add(model);
                }
                Set<KakfaNodeModel> servicesTmp = KafkaNodes;
                KafkaNodes = newKafkaNodes;
                log.info("load KafkaNodes:{}",JSON.toJSONString(KafkaNodes));
                servicesTmp.clear();
            }
        } finally {
            loadNodelock.unlock();
        }

    }

    private KafkaProducer kafkaProducerInstance(ProducerConf conf) {
            Serializer keySerializer = null;
            Serializer valueSerializer = null;
            if(Objects.isNull(conf)){
                conf=new ProducerConf();
            }
            if (StringUtils.isNotBlank(conf.getKeySerializer())) {
                try {
                    keySerializer = (Serializer) Class.forName(conf.getKeySerializer()).newInstance();
                } catch (Exception e) {
                    log.error("To instance KafkaProducer's keySerializer is fail,use default", e);
                }
            }
            if (StringUtils.isNotBlank(conf.getValueSerializer())) {
                try {
                    valueSerializer = (Serializer) Class.forName(conf.getValueSerializer()).newInstance();
                } catch (Exception e) {
                    log.error("To instance KafkaProducer's valueSerializer is fail,use default", e);
                }
            }
            if (StringUtils.isBlank(conf.getBootstrapServers())) {
                conf.setBootstrapServers(getBootstrapServers());
            }
        return new KafkaProducer(conf.toProperties(), keySerializer, valueSerializer);
    }
    public static AdminClient newKafkaAdminClient() {
        Properties prop = new Properties();
        prop.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG,singleton.getBootstrapServers());
        return  AdminClient.create(prop);
    }
    public static KafkaConsumer newConsumer() {
       String serviceType=RegistCenterManagerBuilder.getCurrentRegistCenterManager().getCurrentServiceNode().getServiceType();
        return newConsumer(serviceType);
    }
    /**
     * 获取消费者
     * @return
     */
    public static KafkaConsumer newConsumer(String groupId) {
        if(Objects.isNull(singleton.conf)){
            ConsumerConf consumerConf= new ConsumerConf();
            consumerConf.setGroupId(groupId);
            return singleton.kafkaConsumerInstance(consumerConf);
        }else{
            singleton.conf.getConsumer().setGroupId(groupId);
            return singleton.kafkaConsumerInstance(singleton.conf.getConsumer());
        }


    }

    /**
     * 获取生产者
     * @return
     */
    public static KafkaProducer newProducer() {
        if(Objects.isNull(singleton.conf)){
            return newProducer(new ProducerConf());
        }else {
            return newProducer(singleton.conf.getProducer());
        }
    }

    public static KafkaProducer newProducer(ProducerConf producer) {
        return singleton.kafkaProducerInstance(producer);
    }

    /**
     * 获取 所有 主题
     *
     * @return
     */
    /**
     * 从 zookeeper 获取 brokers 信息
     *
     * @return
     */
    public Set<KakfaNodeModel> getAllBrokersInfo() {
        return KafkaNodes;
    }

    /**
     * 获取 所有 主题
     *
     * @return
     */
    public List<String> getAllTopics() {
        ZkClient zkClient = ZkClientSingleton.instance().getZkClient();
        if (zkClient.exists(kafkaTopicParentPath)) {
            return ZkClientSingleton.instance().getZkClient().getChildren(kafkaTopicParentPath);
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * 根据 主题 获取分区
     *
     * @param topic
     * @return
     */
    public List<String> getPartitionsByTopic(String topic) {
        ZkClient zkClient = ZkClientSingleton.instance().getZkClient();
        String partitionPath = String.format(kafkaPartitionsParenPath, topic);
        if (zkClient.exists(partitionPath)) {
            return zkClient.getChildren(partitionPath);
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * 根据 主题 和 分区 id 获取副本信息
     *
     * @param topic
     * @param partitionid
     * @return String.
     */
    public List<Integer> getReplicasIsr(String topic, int partitionid) {
        ZkClient zkClient = ZkClientSingleton.instance().getZkClient();
        String statePath = String.format(PARTITIONSTATEPath, topic, partitionid);
        if (zkClient.exists(statePath)) {
            JSONArray isrs = JSON.parseObject(zkClient.readData(statePath, true)).getJSONArray("isr");
            List<Integer> isrsList = isrs.toJavaList(Integer.class);
            return isrsList;
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * 根据 topic 获取 副本信息
     *
     * @param topic
     * @return
     */
    public Map<String, Map<String, List<Integer>>> getReplicasByTopic(String topic) {
        Map<String, Map<String, List<Integer>>> replicasMap = new HashMap<>();
        List<String> partitions = getPartitionsByTopic(topic);
        if (partitions.size() > 0) {
            for (int i = 0; i < partitions.size(); i++) {
                List<Integer> replicas = getReplicasIsr(topic, Integer.valueOf(partitions.get(i)));
                Map<String, List<Integer>> partitionReplicas = replicasMap.get(topic);
                if (MapUtils.isEmpty(partitionReplicas)) {
                    partitionReplicas = new HashMap<>();
                    replicasMap.put(topic, partitionReplicas);
                }
                partitionReplicas.put(partitions.get(i), replicas);
            }
        }
        return replicasMap;
    }

    /**
     * 根据 topic 获取 元数据 ，包括 分区 ，learer
     *
     * @param topic
     * @return
     */
    private List<MetadataModel> findKafkaLeaderWithoutReplicas(String topic) {
        List<MetadataModel> targets = new ArrayList<MetadataModel>();
        ZkClient zkClient = ZkClientSingleton.instance().getZkClient();
        if (zkClient.exists(kafkaTopicParentPath)) {
            String topicPath = String.format(kafkaPartitionsParenPath, topic);
            if (zkClient.exists(topicPath)) {
                return targets;
            }
            List<String> partitions = zkClient.getChildren(topicPath);
            if (CollectionUtils.isEmpty(partitions)) {
                return targets;
            }
            for (String partition : partitions) {
                String path = String.format(PARTITIONSTATEPath, topic, partition);
                String state = zkClient.readData(path, true);
                JSONObject stateJson = JSON.parseObject(state);
                MetadataModel metadate = new MetadataModel();
                metadate.setIsr(stateJson.getJSONArray("isr").toJavaList(Integer.class));
                metadate.setLeader(stateJson.getInteger("leader"));
                metadate.setPartitionId(Integer.valueOf(partition));
                targets.add(metadate);
            }
        }
        return targets;
    }

    /**
     * 根据 topic 获取 元数据 ，包括 分区 ，learer ，replicas
     *
     * @param topic
     * @return
     */
    public List<MetadataModel> findMetadataInfoByTopic(String topic) {
        List<MetadataModel> metadataInfos = findKafkaLeaderWithoutReplicas(topic);
        if (CollectionUtils.isNotEmpty(metadataInfos)) {
            for (MetadataModel model : metadataInfos) {
                List<Integer> replicas = getReplicasIsr(topic, model.getPartitionId());
                model.setReplicas(replicas);
                model.setTopic(topic);
            }
        }
        return metadataInfos;
    }

    /**
     * 获取所有消费者组的offset
     *
     * @return
     */
    public Set<OffSetModel> getAllOffsets() {
        Properties prop = new Properties();
        // broker 信息
        prop.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, getBootstrapServers());
        AdminClient client = null;
        Set<OffSetModel> offSetModels = new HashSet<>();
        try {
            client = AdminClient.create(prop);
            Collection<ConsumerGroupListing> groupListings = client.listConsumerGroups().valid().get();
            for (ConsumerGroupListing groupListing : groupListings) {
                Map<TopicPartition, OffsetAndMetadata> offsets = client.listConsumerGroupOffsets(groupListing.groupId()).partitionsToOffsetAndMetadata().get();
                for (Map.Entry<TopicPartition, OffsetAndMetadata> tp : offsets.entrySet()) {
                    OffSetModel model = new OffSetModel();
                    model.setGroupId(groupListing.groupId());
                    model.setTopic(tp.getKey().topic());
                    model.setPartition(tp.getKey().partition());
                    model.setOffset(tp.getValue().offset());
                    model.setLeaderEpoch(tp.getValue().leaderEpoch().equals(Optional.empty()) ? null : tp.getValue().leaderEpoch().get());
                    Map<TopicPartition,Long> topicPartitionLongMap= getKafkaLogSize(tp.getKey().topic(), groupListing.groupId(), Collections.singletonList(tp.getKey().partition()));
                    model.setSize(topicPartitionLongMap.get(tp.getKey()));
                    offSetModels.add(model);
                }
            }
            return offSetModels;
        } catch (Exception e) {
            log.error("To getAllOffsets is fail", e);
            return offSetModels;
        } finally {
            if (!Objects.isNull(client)) {
                client.close();
            }
        }
    }


    /**
     * 根据 主题 ，组 ，分区 获取消息总量
     *
     * @param topic
     * @param groupId
     * @param partitionIds
     * @return
     */
    public static Map<TopicPartition,Long> getKafkaLogSize(String topic, String groupId, List<Integer> partitionIds) {
        KafkaConsumer<String, String> kafkaConsumer = null;
        try {
            kafkaConsumer = newConsumer(groupId);
            return  kafkaConsumer.endOffsets( partitionIds.stream().map( val ->  new TopicPartition(topic, val)).collect(Collectors.toList()));
        } finally {
            if (!Objects.isNull(kafkaConsumer)) {
                kafkaConsumer.close();
            }
        }
    }

    /**
     * 获取 指定 主题 和 消息总量
     *
     * @param topic
     * @return
     */
    public long getTotalLogSizeByTopic(String topic, String groupId) {
        long total = 0;
        List<String> partitions = getPartitionsByTopic(topic);
        Map<TopicPartition,Long> sizes=getKafkaLogSize(topic, groupId, partitions.stream().map(val ->Integer.parseInt(val)).collect(Collectors.toList()));
        for (Long s : sizes.values()) {
            total += s!=null?s:0;
        }
        return total;
    }

    /**
     * 获取offset
     *
     * @param topicName
     */
    public long getOffsets(String topicName, String groupId) {
        long offsets = 0;
        KafkaConsumer<String, String> kafkaConsumer = null;
        try {
            kafkaConsumer = newConsumer(groupId);
            List<PartitionInfo> partitionInfoList = kafkaConsumer.partitionsFor(topicName);
            List<TopicPartition> tpList = new ArrayList<TopicPartition>();
            for (PartitionInfo info : partitionInfoList) {
                int pid = info.partition();
                TopicPartition topicPartition = new TopicPartition(topicName, pid);
                tpList.add(topicPartition);
            }
            for (TopicPartition tp : tpList) {
                long l;
                try {
                    l = kafkaConsumer.committed(tp).offset();
                } catch (Exception e) {
                    l = 0;
                }
                offsets += l;
            }
            kafkaConsumer.close();
            return offsets;
        } finally {
            if (!Objects.isNull(kafkaConsumer)) {
                kafkaConsumer.close();
            }
        }
    }


    public static void main(String[] args) throws Exception {
        ZkConf zkConf=new ZkConf();
        zkConf.setServers("10.10.4.49:2181");
        ZkClientSingleton zkClientSingleton=ZkClientSingleton.buildZkClient(zkConf);
        KafkaClientSingleton kafkaClientSingleton=KafkaClientSingleton.instance(null);
        Properties prop = new Properties();
        prop.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, "10.10.4.49:9092");
        AdminClient client = AdminClient.create(prop);
        Collection<ConsumerGroupListing> consumerGroupListings=client.listConsumerGroups().valid().get();
        System.out.println(JSON.toJSONString(consumerGroupListings.stream().map(val -> val.groupId()).collect(Collectors.toList())));
        //System.out.println(JSON.toJSONString(kafkaClientSingleton.getAllOffsets()));
        List<String> gns=new ArrayList<>();
        gns.add("logcenter_consumer");
        Map<String, KafkaFuture<ConsumerGroupDescription>> stringKafkaFutureMap=client.describeConsumerGroups(gns).describedGroups();
        List<ConsumerGroupDescription> d=stringKafkaFutureMap.values().stream().map(val -> {
            try {
                return val.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }).collect(Collectors.toList());
        System.out.println(JSON.toJSONString(d));
    }

}
