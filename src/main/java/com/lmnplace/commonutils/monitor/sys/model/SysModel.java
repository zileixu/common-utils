package com.lmnplace.commonutils.monitor.sys.model;

import java.util.LinkedList;
import java.util.List;

/**
 * 服务器相关信息
 *
 * @author ruoyi
 */
public class SysModel {

    /**
     * CPU相关信息
     */
    private CpuModel cpu ;

    /**
     * 內存相关信息
     */
    private MemModel mem ;

    /**
     * 內存相关信息
     */
    private SwapModel swap ;

    /**
     * 服务器相关信息
     */
    private SysInfoModel sys ;
    /**
     * 网络相关信息
     */
    private List<NetModel> nets ;
    /**
     * 网络相关信息
     */
    private List<DiskIOModel> diskIOs ;
    /**
     * 磁盘相关信息
     */
    private List<DiskModel> disks = new LinkedList<DiskModel>();

    public CpuModel getCpu() {
        return cpu;
    }

    public void setCpu(CpuModel cpu) {
        this.cpu = cpu;
    }

    public MemModel getMem() {
        return mem;
    }

    public void setMem(MemModel mem) {
        this.mem = mem;
    }

    public SysInfoModel getSys() {
        return sys;
    }

    public void setSys(SysInfoModel sys) {
        this.sys = sys;
    }

    public List<DiskModel> getDisks() {
        return disks;
    }

    public void setDisks(List<DiskModel> disks) {
        this.disks = disks;
    }

    public SwapModel getSwap() {
        return swap;
    }

    public void setSwap(SwapModel swap) {
        this.swap = swap;
    }

    public List<NetModel> getNets() {
        return nets;
    }

    public void setNets(List<NetModel> nets) {
        this.nets = nets;
    }

    public List<DiskIOModel> getDiskIOs() {
        return diskIOs;
    }

    public void setDiskIOs(List<DiskIOModel> diskIOs) {
        this.diskIOs = diskIOs;
    }
}
