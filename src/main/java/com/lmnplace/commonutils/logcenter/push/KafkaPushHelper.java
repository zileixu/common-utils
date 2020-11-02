package com.lmnplace.commonutils.logcenter.push;

import com.alibaba.fastjson.JSON;
import com.lmnplace.commonutils.common.KafkaMessage;
import com.lmnplace.commonutils.common.MessageType;
import com.lmnplace.commonutils.common.kafka.KafkaClientSingleton;
import com.lmnplace.commonutils.common.kafka.KafkaTopic;
import com.lmnplace.commonutils.common.kafka.conf.ProducerConf;
import com.lmnplace.commonutils.common.kafka.conf.kafkaConf;
import com.lmnplace.commonutils.confcenter.ConfCenterManagerBuilder;
import com.lmnplace.commonutils.confcenter.ConfigCenterManager;
import com.lmnplace.commonutils.confcenter.conf.GlobalConfig;
import com.lmnplace.commonutils.logcenter.model.DiscardDataModel;
import com.lmnplace.commonutils.logcenter.model.ExLogModel;
import com.lmnplace.commonutils.logcenter.model.TrackDataModel;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.Future;

public class KafkaPushHelper {
    private static final Logger log = LoggerFactory.getLogger(KafkaPushHelper.class);
    private static KafkaProducer logProducer;
    private static KafkaProducer bizProducer;

    public static KafkaProducer getProducer(ProducerFlag flag) {
        switch (flag){
            case BIZ:
                if (Objects.isNull(bizProducer)) {
                    synchronized (log) {
                        if (Objects.isNull(bizProducer)) {
                            kafkaConf kafkaConf= KafkaClientSingleton.getImmutableConf();
                            //业务数据保证不重复发送
                            kafkaConf.getProducer().setEnableIdempotence(true);
                            bizProducer = KafkaClientSingleton.newProducer(kafkaConf.getProducer());
                        }
                    }
                }
                break;
            case LOG:
            default:
                if (Objects.isNull(logProducer)) {
                    synchronized (log) {
                        if (Objects.isNull(logProducer)) {
                            logProducer = KafkaClientSingleton.newProducer();
                        }
                    }
                }
        }
        if (Objects.isNull(logProducer)) {
            synchronized (log) {
                if (Objects.isNull(logProducer)) {
                    logProducer = KafkaClientSingleton.newProducer();
                }
            }
        }
        return logProducer;
    }

    public static <K, V> long syncBizData(String topic, K key, V value) {
        try {
            getProducer(ProducerFlag.BIZ);
            Future<RecordMetadata> future = logProducer.send(new ProducerRecord(topic, key, value));
            return future.get().offset();
        } catch (Exception e) {
            throw new RuntimeException("To send message to kafka is fail for biz", e);
        }
    }

    private static boolean enableLog(String key) {
        try {
            ConfigCenterManager con = ConfCenterManagerBuilder.getCurrentConfCenterManager();
            return con.getCurrentConf().getBoolean(key, false);
        } catch (Exception e) {
            log.error("get value for {} is fail",key,e);
            return false;
        }
    }
    public static KafkaProducer newKafkaProducer() {
        return KafkaClientSingleton.newProducer();
    }
    public static KafkaProducer newKafkaProducer(ProducerConf conf) {
        return KafkaClientSingleton.newProducer(conf);
    }

    public static <K, V> long syncPush(String topic, K key, V value) {
        try {
            getProducer(ProducerFlag.LOG);
            Future<RecordMetadata> future = logProducer.send(new ProducerRecord(topic, key, value));
            return future.get().offset();
        } catch (Exception e) {
            throw new RuntimeException("To send message to kafka is fail for log", e);
        }
    }

    public static <K, V> Future<RecordMetadata> asyncPush(String topic, K key, V value) {
        getProducer(ProducerFlag.LOG);
        return logProducer.send(new ProducerRecord(topic, key, value));
    }

    public static Future<RecordMetadata> asyncPushDiscardData(DiscardDataModel model) {
        if (enableLog(GlobalConfig.DISCARD_DATA_LOG_ENABLE_KEY)) {
            getProducer(ProducerFlag.LOG);
            Future<RecordMetadata> future = logProducer.send(new ProducerRecord(KafkaTopic.LOG_DISCARD_DATA_TOPIC,
                    String.format("%s_%s", model.getManufacturerCode(), model.getRecordId()), JSON.toJSONString(
                    KafkaMessage.build(MessageType.BZ_DISCARDATA, model))));
            return future;
        } else {
            return null;
        }
    }

    public static Future<RecordMetadata> asyncPushTrackData(TrackDataModel model) {
        if (enableLog(GlobalConfig.TRACK_DATA_LOG_ENABLE_KEY)) {
            getProducer(ProducerFlag.LOG);
            Future<RecordMetadata> future = logProducer.send(new ProducerRecord(KafkaTopic.LOG_TRACKING_DATA_TOPIC,
                    String.format("%s_%s", model.getManufacturerCode(), model.getRecordId()), JSON.toJSONString(
                    KafkaMessage.build(MessageType.BZ_TRACKDATA, model))));
            return future;
        } else {
            return null;
        }
    }

    public static Future<RecordMetadata> asyncPushExLog(ExLogModel model) {
        if (enableLog(GlobalConfig.EX_LOG_ENABLE_KEY)) {
            getProducer(ProducerFlag.LOG);
            Future<RecordMetadata> future = logProducer.send(new ProducerRecord(KafkaTopic.LOG_Ex_TOPIC, JSON.toJSONString(
                    KafkaMessage.build(MessageType.BZ_EX, model))));
            return future;
        } else {
            return null;
        }
    }

    enum ProducerFlag{
        BIZ,LOG
    }
}
