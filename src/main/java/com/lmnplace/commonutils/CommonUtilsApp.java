package com.lmnplace.commonutils;

import com.google.common.collect.Maps;
import com.lmnplace.commonutils.common.kafka.KafkaClientSingleton;
import com.lmnplace.commonutils.common.kafka.conf.kafkaConf;
import com.lmnplace.commonutils.common.zk.ZkClientSingleton;
import com.lmnplace.commonutils.common.zk.ZkConf;
import com.lmnplace.commonutils.confcenter.ConfCenterManagerBuilder;
import com.lmnplace.commonutils.confcenter.ConfigCenterManager;
import com.lmnplace.commonutils.confcenter.conf.CenterConfig;
import com.lmnplace.commonutils.exception.ConfParamsValidationException;
import com.lmnplace.commonutils.globaluuid.GlobalUUIDBuilder;
import com.lmnplace.commonutils.globaluuid.GlobalUUIDGenerater;
import com.lmnplace.commonutils.globaluuid.conf.UUIDConf;
import com.lmnplace.commonutils.monitor.MonitorConf;
import com.lmnplace.commonutils.monitor.jvm.JvmMonitorProcessor;
import com.lmnplace.commonutils.monitor.kafka.KafkaMonitorProcessor;
import com.lmnplace.commonutils.monitor.sys.SysMonitorProcessor;
import com.lmnplace.commonutils.registcenter.RegistCenterManager;
import com.lmnplace.commonutils.registcenter.RegistCenterManagerBuilder;
import com.lmnplace.commonutils.registcenter.conf.NodeConf;
import com.lmnplace.commonutils.registcenter.conf.RegisterCenterConf;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Map;
import java.util.regex.Pattern;

@Component
@Order(1)
public class CommonUtilsApp {
    @Resource
    ZkConf zkConf;
    @Resource
    RegisterCenterConf registerCenterConf;
    @Resource
    NodeConf nodeConf;
    @Resource
    kafkaConf kafkaConf;
    @Resource
    UUIDConf uuidConf;
    @Resource
    MonitorConf monitorConf;
    @Resource
    StandardEnvironment environment;
    @Resource
    CenterConfig centerConfig;
    private String rex=".*application(-[a-zA-Z0-9]+)?\\.properties.*";
    @PostConstruct
    public void init() throws Exception {
        initZkClient(zkConf);
        initRegistcenter(registerCenterConf, nodeConf);
        initConfigCenter(centerConfig, nodeConf);
        initKafkaConsumer(kafkaConf);
        initKafkaProducer(kafkaConf);
        initGlobalUUIDGenerater(uuidConf);
        initKafkaMonitor(monitorConf);
        initSysMonitor(monitorConf);
        initJavaMonitor(monitorConf);
    }

    private ConfigCenterManager initConfigCenter(CenterConfig centerConfig, NodeConf nodeConf) throws Exception {
        return ConfCenterManagerBuilder.buildZkConfCenterManager(centerConfig, nodeConf,convert(environment));
    }

    private Map<String, String> convert(StandardEnvironment environment) {
        Map<String, String> p = Maps.newHashMap();
        environment.getPropertySources().forEach(val->{
            if(Pattern.matches(rex, val.getName())&& val.getSource() instanceof Map){
                ((Map)val.getSource()).entrySet().stream().forEach(v->{
                    Map.Entry v1=((Map.Entry)v);
                    p.put((String) v1.getKey(), (String) v1.getValue());
                });
            }
        });
        return p;
    }
    public ZkClientSingleton initZkClient(ZkConf zkConf) throws Exception {
        return ZkClientSingleton.buildZkClient(zkConf);
    }

    public RegistCenterManager initRegistcenter(RegisterCenterConf registerCenterConf, NodeConf nodeConf) throws IllegalAccessException, InstantiationException, ClassNotFoundException, ConfParamsValidationException {
        return RegistCenterManagerBuilder.buildZkRegistCenterManager(registerCenterConf, nodeConf);
    }
    public KafkaConsumer initKafkaConsumer(kafkaConf kafkaConf) {
        return KafkaClientSingleton.instance(kafkaConf).newConsumer();
    }

    public KafkaProducer initKafkaProducer(kafkaConf kafkaConf) {
        return KafkaClientSingleton.instance(kafkaConf).newProducer();
    }

    public GlobalUUIDGenerater initGlobalUUIDGenerater(UUIDConf uuidConf) {
        return GlobalUUIDBuilder.buildZkUUIDGenerater(uuidConf);
    }
    public KafkaMonitorProcessor initKafkaMonitor(MonitorConf monitorConf) {
        return new KafkaMonitorProcessor(monitorConf.getKafka());
    }
    public SysMonitorProcessor initSysMonitor(MonitorConf monitorConf) {
        return new SysMonitorProcessor(monitorConf.getSys());
    }
    public JvmMonitorProcessor initJavaMonitor(MonitorConf monitorConf) {
        return new JvmMonitorProcessor(monitorConf.getJvm());
    }
}
