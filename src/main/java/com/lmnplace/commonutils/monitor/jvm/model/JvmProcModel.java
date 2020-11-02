package com.lmnplace.commonutils.monitor.jvm.model;

public class JvmProcModel {
    private ProcCpuModel procCpu;
    private JvmModel jvm;
    private GcModel gc;
    private ThreadModel thread;

    public ProcCpuModel getProcCpu() {
        return procCpu;
    }

    public void setProcCpu(ProcCpuModel procCpu) {
        this.procCpu = procCpu;
    }

    public JvmModel getJvm() {
        return jvm;
    }

    public void setJvm(JvmModel jvm) {
        this.jvm = jvm;
    }

    public GcModel getGc() {
        return gc;
    }

    public void setGc(GcModel gc) {
        this.gc = gc;
    }

    public ThreadModel getThread() {
        return thread;
    }

    public void setThread(ThreadModel thread) {
        this.thread = thread;
    }
}
