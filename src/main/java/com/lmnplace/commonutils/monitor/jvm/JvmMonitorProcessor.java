package com.lmnplace.commonutils.monitor.jvm;

import com.alibaba.fastjson.JSON;
import com.lmnplace.commonutils.common.KafkaMessage;
import com.lmnplace.commonutils.common.MessageType;
import com.lmnplace.commonutils.common.kafka.KafkaClientSingleton;
import com.lmnplace.commonutils.common.kafka.KafkaTopic;
import com.lmnplace.commonutils.common.sigar.SigarSingleton;
import com.lmnplace.commonutils.monitor.jvm.common.GcGenerationAge;
import com.lmnplace.commonutils.monitor.jvm.common.Javav;
import com.lmnplace.commonutils.monitor.jvm.common.JvmZone;
import com.lmnplace.commonutils.monitor.jvm.conf.JvmMonitorConf;
import com.lmnplace.commonutils.monitor.jvm.model.*;
import com.lmnplace.commonutils.monitor.kafka.KafkaMonitorProcessor;
import com.lmnplace.commonutils.utils.Arith;
import com.lmnplace.commonutils.utils.ValidationUtil;
import com.sun.management.GcInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.hyperic.sigar.ProcCpu;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.util.Util;
import sun.management.ManagementFactoryHelper;

import java.lang.management.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class JvmMonitorProcessor {
    private static long WAIT_TIME = 500;
    private static final Logger log = LoggerFactory.getLogger(JvmMonitorProcessor.class);
    private JvmMonitorConf javaConf;
    private ScheduledExecutorService executorService;
    private KafkaProducer kafkaProducer = null;
    public JvmMonitorProcessor(JvmMonitorConf javaConf) {
        ValidationUtil.validate(javaConf);
        this.javaConf = javaConf;
        if (javaConf.isEnable()) {
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
    private void collectMonitorData() {
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    JvmProcModel jvmProcModel = new JvmProcModel();
                    jvmProcModel.setProcCpu(getProcCpuModel());
                    jvmProcModel.setJvm(getJvmModel());
                    jvmProcModel.setThread(getThreadMoDel());
                    jvmProcModel.setGc(getGcModel());
                    kafkaProducer = getKafkaProducer();
                    kafkaProducer.send(new ProducerRecord(KafkaTopic.LOG_MONITOR_RESOURCE_TOPIC, JSON.toJSONString(KafkaMessage.build(MessageType.MT_JVMPRO,jvmProcModel))));
                } catch (Error|Exception e) {
                    log.error(" To get monitor info is fail by sigar ", e);
                }

            }
        }, javaConf.getIntervalTime(), javaConf.getIntervalTime(), TimeUnit.MILLISECONDS);
    }

    private ProcCpuModel getProcCpuModel() {
        ProcCpuModel procCpuModel=new ProcCpuModel();
        Sigar sigar=SigarSingleton.getSigarInstance();
        try {
            procCpuModel.setPid(sigar.getPid());
            ProcCpu cpu1 =sigar.getProcCpu(sigar.getPid());
            Util.sleep(WAIT_TIME);
            ProcCpu cpu2 =sigar.getProcCpu(sigar.getPid());
            long interval=cpu2.getLastTime()-cpu1.getLastTime();
            if(interval>0){
                procCpuModel.setCpuSysRate(Arith.mul(Arith.div((cpu2.getSys()-cpu1.getSys()), interval, 4), 100));
                procCpuModel.setCpuUserRate(Arith.mul(Arith.div((cpu2.getUser()-cpu1.getUser()), interval, 4), 100));
                procCpuModel.setCpuTotalRate(Arith.mul(Arith.round(cpu2.getPercent(),4),100));
            }else {
                procCpuModel.setCpuSysRate(0);
                procCpuModel.setCpuUserRate(0);
                procCpuModel.setCpuTotalRate(0);
            }
        } catch (SigarException e) {
            log.error("To get cpu info is fail",e);
        }
        return procCpuModel;
    }

    private ThreadModel getThreadMoDel() {
        ThreadModel thread = new ThreadModel();
        ThreadMXBean threadBean = ManagementFactoryHelper.getThreadMXBean();
        thread.setCollectTime(System.currentTimeMillis());
        thread.setTotalCount(threadBean.getTotalStartedThreadCount());
        thread.setDaemonCount(threadBean.getDaemonThreadCount());
        thread.setPeekCount(threadBean.getPeakThreadCount());
        long[] ids = null;
        ThreadInfo[] infos = null;
        if (ArrayUtils.isNotEmpty(ids = threadBean.getAllThreadIds())
                && ArrayUtils.isNotEmpty(infos = threadBean.getThreadInfo(ids))) {
            int newC = 0, runC = 0, blockedC = 0, waitingC = 0, timedWaitC = 0, terminatedC = 0;
            for (ThreadInfo info : infos) {
                switch (info.getThreadState()) {
                    case NEW:
                        ++newC;
                        break;
                    case RUNNABLE:
                        ++runC;
                        break;
                    case BLOCKED:
                        ++blockedC;
                        break;
                    case WAITING:
                        ++waitingC;
                        break;
                    case TIMED_WAITING:
                        ++timedWaitC;
                        break;
                    case TERMINATED:
                        ++terminatedC;
                        break;
                    default:
                        break;
                }
            }
            thread.setNewCount(newC);
            thread.setRunnableCount(runC);
            thread.setBlockedCount(blockedC);
            thread.setWaitingCount(waitingC);
            thread.setTimedWaitingCount(timedWaitC);
            thread.setTerminatedCount(terminatedC);
        }

        return thread;
    }

    private JvmModel getJvmModel() {
        JvmModel jvmModel = new JvmModel();
        MemoryMXBean memoryMXBean = ManagementFactoryHelper.getMemoryMXBean();
        if (!Objects.isNull(memoryMXBean)) {
            jvmModel.setWaitGcObjectCount(memoryMXBean.getObjectPendingFinalizationCount());
            MemoryUsage heap = memoryMXBean.getHeapMemoryUsage();
            jvmModel.setHeapSpace(heap.getMax());
            jvmModel.setHeapUsed(heap.getUsed());
            MemoryUsage noHeap = memoryMXBean.getNonHeapMemoryUsage();
            jvmModel.setNoHeapSpace(noHeap.getMax());
            jvmModel.setNoHeapUsed(noHeap.getUsed());
        }
        jvmModel.setJdkVersion(Javav.version());
        return jvmModel;
    }

    private static GcModel getGcModel() {
        GcModel gcModel = new GcModel();
        List<GarbageCollectorMXBean> beans = ManagementFactoryHelper.getGarbageCollectorMXBeans();
        if (CollectionUtils.isNotEmpty(beans)) {
            for (GarbageCollectorMXBean bean : beans) {
                com.sun.management.GarbageCollectorMXBean mxBean = (com.sun.management.GarbageCollectorMXBean) bean;
                GcGenerationAge gcGenerationAge = GcGenerationAge.fromName(bean.getName());
                if (gcGenerationAge.equals(GcGenerationAge.YOUNG)) {
                    gcModel.setTotalYgc(bean.getCollectionCount());
                    gcModel.setTotalYgct(bean.getCollectionTime());
                    GcInfo gcInfo = mxBean.getLastGcInfo();
                    Map<String, MemoryUsage> beforeGc = gcInfo.getMemoryUsageBeforeGc();
                    Map<String, MemoryUsage> afterGc = gcInfo.getMemoryUsageAfterGc();
                    if (MapUtils.isEmpty(beforeGc) || MapUtils.isEmpty(afterGc)) {
                        continue;
                    }
                    for (Map.Entry<String, MemoryUsage> entry : beforeGc.entrySet()) {
                        MemoryUsage memoryUsage = entry.getValue();
                        switch (entry.getKey()) {
                            case JvmZone.PSSS:
                                gcModel.setSs(memoryUsage.getMax());
                                gcModel.setSu(memoryUsage.getUsed());
                                gcModel.setLastGcSu(memoryUsage.getUsed() - afterGc.get(entry.getKey()).getUsed());
                                break;
                            case JvmZone.PSES:
                                gcModel.setEs(memoryUsage.getMax());
                                gcModel.setEu(memoryUsage.getUsed());
                                gcModel.setLastGcEu(memoryUsage.getUsed() - afterGc.get(entry.getKey()).getUsed());
                                break;
                            case JvmZone.MS:
                                gcModel.setMs(memoryUsage.getMax());
                                gcModel.setMu(memoryUsage.getUsed());
                                gcModel.setLastGcMu(memoryUsage.getUsed() - afterGc.get(entry.getKey()).getUsed());
                                break;
                            case JvmZone.PSOG:
                                gcModel.setOs(memoryUsage.getMax());
                                gcModel.setOu(memoryUsage.getUsed());
                                gcModel.setLastGcOu(memoryUsage.getUsed() - afterGc.get(entry.getKey()).getUsed());
                                break;
                            case JvmZone.CCS:
                                gcModel.setCcss(memoryUsage.getMax());
                                gcModel.setCcsu(memoryUsage.getUsed());
                                gcModel.setLastGcCccsu(memoryUsage.getUsed() - afterGc.get(entry.getKey()).getUsed());
                                break;
                            default:
                                break;
                        }
                    }
                } else if (gcGenerationAge.equals(GcGenerationAge.OLD)) {
                    gcModel.setTotalFgc(bean.getCollectionCount());
                    gcModel.setTotalFgct(bean.getCollectionTime());
                    GcInfo gcInfo = mxBean.getLastGcInfo();
                    Map<String, MemoryUsage> beforeGc = gcInfo.getMemoryUsageBeforeGc();
                    Map<String, MemoryUsage> afterGc = gcInfo.getMemoryUsageAfterGc();
                    if (MapUtils.isEmpty(beforeGc) || MapUtils.isEmpty(afterGc)) {
                        continue;
                    }
                    for (Map.Entry<String, MemoryUsage> entry : beforeGc.entrySet()) {
                        MemoryUsage memoryUsage = entry.getValue();
                        switch (entry.getKey()) {
                            case JvmZone.MS:
                                gcModel.setLastGcMu(memoryUsage.getUsed() - afterGc.get(entry.getKey()).getUsed());
                                break;
                            case JvmZone.PSOG:
                                gcModel.setLastGcOu(memoryUsage.getUsed() - afterGc.get(entry.getKey()).getUsed());
                                break;
                            case JvmZone.CCS:
                                gcModel.setLastGcCccsu(memoryUsage.getUsed() - afterGc.get(entry.getKey()).getUsed());
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        }
        return gcModel;
    }
}
