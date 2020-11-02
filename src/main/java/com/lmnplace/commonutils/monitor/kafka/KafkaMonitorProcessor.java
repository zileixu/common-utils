package com.lmnplace.commonutils.monitor.kafka;

import com.alibaba.fastjson.JSON;
import com.lmnplace.commonutils.common.KafkaMessage;
import com.lmnplace.commonutils.common.MessageType;
import com.lmnplace.commonutils.common.kafka.KafkaClientSingleton;
import com.lmnplace.commonutils.common.kafka.KafkaTopic;
import com.lmnplace.commonutils.common.zk.ZkClientSingleton;
import com.lmnplace.commonutils.common.zk.ZkConf;
import com.lmnplace.commonutils.monitor.kafka.conf.kafkaMonitorConf;
import com.lmnplace.commonutils.monitor.kafka.model.*;
import com.lmnplace.commonutils.utils.ValidationUtil;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.TopicPartitionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class KafkaMonitorProcessor {
    private static final Logger log = LoggerFactory.getLogger(KafkaMonitorProcessor.class);
    private kafkaMonitorConf kafkaConf;
    private ScheduledExecutorService executorService;
    AdminClient adminClient = null;
    KafkaProducer kafkaProducer = null;
    KafkaConsumer consumer=null;
    public KafkaMonitorProcessor() {
    }

    public KafkaMonitorProcessor(kafkaMonitorConf kafkaConf) {
        ValidationUtil.validate(kafkaConf);
        this.kafkaConf = kafkaConf;
        if (kafkaConf.isEnable()) {
            executorService = Executors.newSingleThreadScheduledExecutor();
            collectMonitorData();
        }
    }

    private KafkaProducer getKafkaProducer() {
        if (kafkaProducer != null) {
            return kafkaProducer;
        }
        synchronized (KafkaMonitorProcessor.class) {
            if (kafkaProducer != null) {
                return kafkaProducer;
            }
            kafkaProducer = KafkaClientSingleton.newProducer();
            return kafkaProducer;
        }
    }
    private KafkaConsumer getKafkaConsumer() {
        if (consumer != null) {
            return consumer;
        }
        synchronized (KafkaMonitorProcessor.class) {
            if (consumer != null) {
                return consumer;
            }
            consumer = KafkaClientSingleton.newConsumer();
            return consumer;
        }
    }
    private void collectMonitorData() {
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    kafkaProducer = getKafkaProducer();
                    KafkaMonitorModel kafkaMonitorModel=new KafkaMonitorModel();
                    kafkaMonitorModel.setTopics(genTopicModels());
                    kafkaMonitorModel.setGroups(genConsumerGroupModels());
                    kafkaProducer.send(new ProducerRecord(KafkaTopic.LOG_MONITOR_RESOURCE_TOPIC, JSON.toJSONString(KafkaMessage.build(MessageType.MT_KAFKA, kafkaMonitorModel))));
                } catch (Exception e) {
                    log.error("To get monitor info is fail for kafka", e);
                }
            }
        }, kafkaConf.getIntervalTime(), kafkaConf.getIntervalTime(), TimeUnit.MILLISECONDS);
    }

    private List<ConsumerGroupModel> genConsumerGroupModels() throws ExecutionException, InterruptedException {
        adminClient = getAdminClient();
        Collection<ConsumerGroupListing> consumerGroupListings = adminClient.listConsumerGroups().valid().get();
        List<String> groupIds = consumerGroupListings.stream().map(val -> val.groupId()).collect(Collectors.toList());
        Map<String, KafkaFuture<ConsumerGroupDescription>> groupDescris = adminClient.describeConsumerGroups(groupIds).describedGroups();
        List<ConsumerGroupModel> models = new LinkedList<>();
        for (KafkaFuture<ConsumerGroupDescription> val : groupDescris.values()) {
            ConsumerGroupModel model = new ConsumerGroupModel();
            ConsumerGroupDescription description = val.get();
            Collection<MemberDescription> memberDescriptions = description.members();
            if (CollectionUtils.isEmpty(memberDescriptions)) {
                continue;
            }
            model.setMemebers(getMemberModels(memberDescriptions, description.groupId(), adminClient));
            model.setGroupId(description.groupId());
            model.setCoordinator(new BrokerModel(description.coordinator().host(), description.coordinator().port()));
            model.setPartitionAssignor(description.partitionAssignor());
            model.setState(description.state().toString());
            models.add(model);
        }
        return models;
    }

    private List<MemberModel> getMemberModels(Collection<MemberDescription> memberDescriptions, String groupId, AdminClient adminClient) throws ExecutionException, InterruptedException {
        List<MemberModel> memberModels = new LinkedList<>();
        for (MemberDescription member : memberDescriptions) {
            MemberModel memberModel = new MemberModel();
            memberModel.setClientId(member.clientId());
            memberModel.setHost(member.host());
            memberModel.setConsumerId(member.consumerId());
            Set<TopicPartition> topicPartitions = member.assignment().topicPartitions();
            List<TopicPartitionModel> models = new LinkedList<>();
            Map<TopicPartition, OffsetSpec> topicPartitionOffsets = new HashMap<>();
            for (TopicPartition topicPartition : topicPartitions) {
                topicPartitionOffsets.put(topicPartition, new OffsetSpec());
            }
            Map<TopicPartition, ListOffsetsResult.ListOffsetsResultInfo> offsetsResultInfoMap = adminClient.listOffsets(topicPartitionOffsets).all().get();
            KafkaConsumer consumer = getKafkaConsumer();
            Map<TopicPartition, Long> logSizes = consumer.endOffsets(offsetsResultInfoMap.keySet());
            for (Map.Entry<TopicPartition, Long> logSize : logSizes.entrySet()) {
                TopicPartitionModel model = new TopicPartitionModel();
                model.setTopic(logSize.getKey().topic());
                model.setPartition(logSize.getKey().partition());
                model.setOffset(offsetsResultInfoMap.get(logSize.getKey()).offset());
                model.setSize(logSize.getValue());
                models.add(model);
            }
            memberModel.setTopicPartitions(models);
            memberModels.add(memberModel);
        }
        return memberModels;
    }

    private AdminClient getAdminClient() {
        if (adminClient != null) {
            return adminClient;
        }
        synchronized (KafkaMonitorProcessor.class) {
            if (adminClient != null) {
                return adminClient;
            }
            adminClient = KafkaClientSingleton.newKafkaAdminClient();
            return adminClient;
        }
    }
    private List<TopicModel> genTopicModels() throws ExecutionException, InterruptedException {
        adminClient = getAdminClient();
        Collection<String> topics = adminClient.listTopics().names().get();
        Map<String, TopicDescription> topicDescriptionMap = adminClient.describeTopics(topics).all().get();
        List<TopicModel> topicModels = new LinkedList<>();
        for (TopicDescription description : topicDescriptionMap.values()) {
            TopicModel model = new TopicModel();
            model.setTopic(description.name());
            model.setInternal(description.isInternal());
            if (CollectionUtils.isEmpty(description.partitions())) {
                continue;
            }
            List<TopicPartition> topicPartitions = description.partitions().stream().map(val -> new TopicPartition(description.name(), val.partition())).collect(Collectors.toList());
            List<PartitionModel> partitionModels = new LinkedList<>();
            KafkaConsumer consumer = getKafkaConsumer();
            Map<TopicPartition, Long> logSizes = consumer.endOffsets(topicPartitions);
            for (TopicPartitionInfo info : description.partitions()) {
                PartitionModel partitionModel = new PartitionModel();
                partitionModel.setLeader(BrokerModel.build(info.leader()));
                partitionModel.setIsr(BrokerModel.build(info.isr()));
                partitionModel.setReplicas(BrokerModel.build(info.replicas()));
                partitionModel.setPartition(info.partition());
                partitionModel.setSize(logSizes.get(new TopicPartition(description.name(), info.partition())));
                partitionModels.add(partitionModel);
            }
            model.setPartitions(partitionModels);
            topicModels.add(model);
        }
        return topicModels;
    }

    public static void main(String[] args) throws Exception {
        ZkConf zkConf = new ZkConf();
        zkConf.setServers("10.10.4.49:2181");
        ZkClientSingleton zkClientSingleton = ZkClientSingleton.buildZkClient(zkConf);
        KafkaMonitorProcessor processor = new KafkaMonitorProcessor();
        System.out.println(JSON.toJSONString(processor.genConsumerGroupModels()));
        System.out.println(JSON.toJSONString(processor.genTopicModels()));
    }
}
