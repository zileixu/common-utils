package com.lmnplace.commonutils.monitor.sys;

import com.alibaba.fastjson.JSON;
import com.lmnplace.commonutils.common.KafkaMessage;
import com.lmnplace.commonutils.common.MessageType;
import com.lmnplace.commonutils.common.kafka.KafkaClientSingleton;
import com.lmnplace.commonutils.common.kafka.KafkaTopic;
import com.lmnplace.commonutils.monitor.kafka.KafkaMonitorProcessor;
import com.lmnplace.commonutils.monitor.sys.conf.SysMonitorConf;
import com.lmnplace.commonutils.monitor.sys.model.*;
import com.lmnplace.commonutils.registcenter.RegistCenterManager;
import com.lmnplace.commonutils.registcenter.RegistCenterManagerBuilder;
import com.lmnplace.commonutils.registcenter.mode.ServiceNode;
import com.lmnplace.commonutils.utils.Arith;
import com.lmnplace.commonutils.utils.IpUtil;
import com.lmnplace.commonutils.utils.ValidationUtil;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import oshi.util.Util;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SysMonitorProcessor {
    private static final Logger log = LoggerFactory.getLogger(SysMonitorProcessor.class);
    private static long WAIT_TIME = 500;
    private SysMonitorConf sysConf;
    private ScheduledExecutorService executorService;
    private KafkaProducer kafkaProducer = null;
    public SysMonitorProcessor() {
    }

    public SysMonitorProcessor(SysMonitorConf sysConf) {
        ValidationUtil.validate(sysConf);
        this.sysConf = sysConf;
        if (sysConf.isEnable()) {
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
                    SystemInfo si = new SystemInfo();
                    HardwareAbstractionLayer hal = si.getHardware();
                    long[] firstTicks = hal.getProcessor().getSystemCpuLoadTicks();
                    List<NetworkIF> firstNets = hal.getNetworkIFs();
                    List<HWDiskStore> firstHwDiskStores=hal.getDiskStores();
                    //休眠时间不可调整，以下步骤使用到此数据做逻辑运算
                    Util.sleep(WAIT_TIME);
                    long[] secondTicks = hal.getProcessor().getSystemCpuLoadTicks();
                    List<NetworkIF> secondNets = hal.getNetworkIFs();
                    List<HWDiskStore> secondHwDiskStores=hal.getDiskStores();
                    SysModel sysModel= new SysModel();
                    sysModel.setDiskIOs(getDiskIOInfo(firstHwDiskStores,secondHwDiskStores));
                    sysModel.setCpu(getCpuInfo(firstTicks,secondTicks,hal.getProcessor().getLogicalProcessorCount()));
                    sysModel.setNets(getNetInfo(firstNets,secondNets));
                    sysModel.setMem(getMemInfo(hal.getMemory()));
                    sysModel.setSwap(getSwapMemInfo(hal.getMemory().getVirtualMemory()));
                    sysModel.setSys(getSysInfo());
                    sysModel.setDisks(getSysFiles(si.getOperatingSystem()));
                    kafkaProducer = getKafkaProducer();
                    kafkaProducer.send(new ProducerRecord(KafkaTopic.LOG_MONITOR_RESOURCE_TOPIC, JSON.toJSONString(KafkaMessage.build(MessageType.MT_SYS,sysModel))));
                } catch (Error |Exception e) {
                    log.error(" To get monitor info is fail by oshi ", e);
                }

            }
        }, sysConf.getIntervalTime(), sysConf.getIntervalTime(), TimeUnit.MILLISECONDS);
    }
    private List<DiskIOModel> getDiskIOInfo(List<HWDiskStore> firstDiskStores,List<HWDiskStore> secondDiskStores) {
        int size=secondDiskStores.size();
        List<DiskIOModel> diskIOModels = new LinkedList<DiskIOModel>();
        for(int i=0;i<size;i++){
            DiskIOModel model=new DiskIOModel();
            HWDiskStore store2=secondDiskStores.get(i);
            HWDiskStore store1=firstDiskStores.get(i);
            model.setCollectTime(store2.getTimeStamp());
            model.setName(store2.getName());
            model.setSize(store2.getSize());
            model.setReadByes(store2.getReadBytes());
            model.setReadTimes(store2.getReads());
            model.setWriteByes(store2.getWriteBytes());
            model.setWriteTimes(store2.getWrites());
            //io字节数计算单位为秒，因收集数据间隔为500毫秒，故此处需要乘以2
            model.setIoBytesS(((store2.getReadBytes()+store2.getWriteBytes())-(store1.getReadBytes()+store1.getWriteBytes()))*2);
            long totalTime=store2.getTimeStamp()-store1.getTimeStamp();
            model.setIoRate(Arith.mul(totalTime<=0?0: Arith.div((store2.getTransferTime()-store1.getTransferTime()), totalTime, 4), 100));
            diskIOModels.add(model);
        }
        return diskIOModels;
    }

    /**
     * 设置CPU信息
     */
    private CpuModel getCpuInfo(long[] prevTicks , long[] ticks,int processorCount)
    {
        CpuModel cpu = new CpuModel();
        // CPU信息
        long nice = ticks[CentralProcessor.TickType.NICE.getIndex()] - prevTicks[CentralProcessor.TickType.NICE.getIndex()];
        long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()] - prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
        long softirq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()] - prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
        long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()] - prevTicks[CentralProcessor.TickType.STEAL.getIndex()];
        long cSys = ticks[CentralProcessor.TickType.SYSTEM.getIndex()] - prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
        long user = ticks[CentralProcessor.TickType.USER.getIndex()] - prevTicks[CentralProcessor.TickType.USER.getIndex()];
        long iowait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()] - prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
        long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()] - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
        long totalCpu = user + nice + cSys + idle + iowait + irq + softirq + steal;
        cpu.setCpuNum(processorCount);
        cpu.setSysRate(totalCpu<=0?0:Arith.mul(Arith.div(cSys, totalCpu, 4), 100));
        cpu.setUserRate(totalCpu<=0?0:Arith.mul(Arith.div(user, totalCpu, 4), 100));
        cpu.setWaitRate(totalCpu<=0?0:Arith.mul(Arith.div(iowait, totalCpu, 4), 100));
        cpu.setFreeRate(totalCpu<=0?0:Arith.mul(Arith.div(idle, totalCpu, 4), 100));
        return cpu;
    }

    /**
     * 获取内存信息
     */
    private List<NetModel> getNetInfo(List<NetworkIF> firstNetworkIFS,List<NetworkIF> secondNetworkIFS) {
        List<NetModel> netModels = new LinkedList<NetModel>();
        int size=secondNetworkIFS.size();
        for (int i=0;i<size;i++) {
            NetworkIF net2=secondNetworkIFS.get(i);
            NetworkIF net1=firstNetworkIFS.get(i);
            NetModel model=new NetModel();
            model.setCollectTime(net2.getTimeStamp());
            model.setName(net2.getName());
            model.setBytesRecv(net2.getBytesRecv());
            model.setBytesSent(net2.getBytesSent());
            model.setBytesSpeed(net2.getSpeed());
            model.setPacketsRecv(net2.getPacketsRecv());
            model.setPacketsSent(net2.getPacketsSent());
            //速率计算单位为秒，因收集数据间隔为500毫秒，故此处需要乘以2
            model.setBytesUseSpeed(((net2.getBytesRecv()-net1.getBytesRecv())+(net2.getBytesSent()-net1.getBytesSent())*2));
            model.setUseRate(model.getBytesSpeed()<=0?0:Arith.mul(Arith.div(model.getBytesUseSpeed(), model.getBytesSpeed(), 4), 100));
            netModels.add(model);
        }
        return netModels;
    }


    /**
     * 获取内存信息
     */
    private MemModel getMemInfo(GlobalMemory memory)
    {
        MemModel mem = new MemModel();
        mem.setTotal(memory.getTotal());
        mem.setUsed(memory.getTotal() - memory.getAvailable());
        mem.setFree(memory.getAvailable());
        return mem;
    }
    /**
     * 获取swap信息
     */
    private SwapModel getSwapMemInfo(VirtualMemory memory)
    {
        SwapModel mem = new SwapModel();
        mem.setTotal(memory.getSwapTotal());
        mem.setUsed(memory.getSwapUsed());
        mem.setFree(memory.getSwapTotal()-memory.getSwapUsed());
        return mem;
    }
    /**
     * 获取服务器信息
     */
    private SysInfoModel getSysInfo()
    {
        SysInfoModel sys = new SysInfoModel();
        Properties props = System.getProperties();
        RegistCenterManager registCenterManager=RegistCenterManagerBuilder.getCurrentRegistCenterManager();
        ServiceNode serviceNode =null;
        if(Objects.isNull(registCenterManager)||Objects.isNull(serviceNode=registCenterManager.getCurrentServiceNode())){
            sys.setComputerName(IpUtil.getHostName());
            sys.setComputerIp(IpUtil.getHostIp());
        }else {
            sys.setComputerName(serviceNode.getHostName());
            sys.setComputerIp(serviceNode.getIp());
        }
        sys.setOsName(props.getProperty("os.name"));
        sys.setOsArch(props.getProperty("os.arch"));
        sys.setUserDir(props.getProperty("user.dir"));
        return sys;
    }



    /**
     * 获取磁盘信息
     */
    private List<DiskModel> getSysFiles(OperatingSystem os)
    {
        List<DiskModel> sysFiles = new LinkedList<DiskModel>();
        FileSystem fileSystem = os.getFileSystem();
        List<OSFileStore> stores = fileSystem.getFileStores();
        for (OSFileStore fs : stores)
        {
            long free = fs.getUsableSpace();
            long total = fs.getTotalSpace();
            long used = total - free;
            DiskModel sysFile = new DiskModel();
            sysFile.setDirName(fs.getMount());
            sysFile.setSysTypeName(fs.getType());
            sysFile.setTypeName(fs.getName());
            sysFile.setTotal(total);
            sysFile.setFree(free);
            sysFile.setUsed(used);
            sysFile.setUsedRate(total<=0?0:Arith.mul(Arith.div(used, total, 4), 100));
            sysFiles.add(sysFile);
        }
        return sysFiles;
    }
}
