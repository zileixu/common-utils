package com.lmnplace.commonutils.monitor.jvm.common;

import com.lmnplace.commonutils.monitor.jvm.model.JstackModel;
import com.lmnplace.commonutils.utils.ArrayUtil;
import com.lmnplace.commonutils.utils.CmdResult;
import com.lmnplace.commonutils.utils.CommandUtil;
import com.lmnplace.commonutils.utils.PathUtil;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.charset.Charset;

public class Jstack {
    private final static String prefix = "java.lang.Thread.State: ";

    /**
     * 该进程的线程信息
     * X轴为时间，Y轴为值的变化
     *
     * @return
     */
    public static JstackModel jstack() throws IOException {
        String id = getPid();
        CmdResult s = CommandUtil.exeCommand("jstack " + id);
        int total = ArrayUtil.appearNumber(s.getMsg(), "nid=");
        int RUNNABLE = ArrayUtil.appearNumber(s.getMsg(), prefix + "RUNNABLE");
        int TIMED_WAITING = ArrayUtil.appearNumber(s.getMsg(), prefix + "TIMED_WAITING");
        int WAITING = ArrayUtil.appearNumber(s.getMsg(), prefix + "WAITING");
        return new JstackModel(id, total, RUNNABLE, TIMED_WAITING, WAITING);
    }

    /**
     * 导出线程快照
     *
     * @return
     */
    public static String dump() throws IOException {
        String id = getPid();
        String path = PathUtil.getRootPath("dump/" + id + "_thread.txt");
        CmdResult s = CommandUtil.exeCommand("jstack" + id);
        File file = new File(path);
        FileUtils.write(file, s.getMsg(), Charset.forName("UTF-8"));
        return path;
    }

    /**
     * 获取当前应用进程id
     *
     * @return
     */
    public static String getPid() {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        String pid = name.split("@")[0];
        return pid;
    }
}
