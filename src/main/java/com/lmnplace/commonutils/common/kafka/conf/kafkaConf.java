package com.lmnplace.commonutils.common.kafka.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "kafka")
@EnableConfigurationProperties
@Component
public class kafkaConf {
    private ConsumerConf consumer;
    private ProducerConf producer;

    public ConsumerConf getConsumer() {
        return consumer;
    }

    public void setConsumer(ConsumerConf consumer) {
        this.consumer = consumer;
    }

    public ProducerConf getProducer() {
        return producer;
    }

    public void setProducer(ProducerConf producer) {
        this.producer = producer;
    }
}
