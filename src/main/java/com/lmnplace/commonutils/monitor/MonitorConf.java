package com.lmnplace.commonutils.monitor;

import com.lmnplace.commonutils.monitor.jvm.conf.JvmMonitorConf;
import com.lmnplace.commonutils.monitor.kafka.conf.kafkaMonitorConf;
import com.lmnplace.commonutils.monitor.sys.conf.SysMonitorConf;
import com.lmnplace.commonutils.monitor.zk.conf.ZkMonitorConf;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 监控配置文件
 * 启动zk的监控
 * monitor.zk.enable=true
 * 每个10秒收集一次数据
 * monitor.zk.intervalTime=60000
 */
@ConfigurationProperties(prefix = "monitor")
@EnableConfigurationProperties
@Component
public class MonitorConf {
    private JvmMonitorConf jvm = new JvmMonitorConf();
    private ZkMonitorConf zk = new ZkMonitorConf();
    private kafkaMonitorConf kafka = new kafkaMonitorConf();
    private SysMonitorConf sys = new SysMonitorConf();

    public JvmMonitorConf getJvm() {
        return jvm;
    }

    public void setJvm(JvmMonitorConf jvm) {
        this.jvm = jvm;
    }

    public ZkMonitorConf getZk() {
        return zk;
    }

    public void setZk(ZkMonitorConf zk) {
        this.zk = zk;
    }

    public kafkaMonitorConf getKafka() {
        return kafka;
    }

    public void setKafka(kafkaMonitorConf kafka) {
        this.kafka = kafka;
    }

    public SysMonitorConf getSys() {
        return sys;
    }

    public void setSys(SysMonitorConf sys) {
        this.sys = sys;
    }
}
