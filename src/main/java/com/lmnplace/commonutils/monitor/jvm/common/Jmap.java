package com.lmnplace.commonutils.monitor.jvm.common;
import com.lmnplace.commonutils.common.sigar.SigarSingleton;
import com.lmnplace.commonutils.utils.CommandUtil;
import com.lmnplace.commonutils.utils.PathUtil;

import java.io.File;
import java.io.IOException;

public class Jmap {

    /**
     * 导出堆快照
     * @return
     */
    public static String dump() throws IOException {
        String id=getPid();
        String path = PathUtil.getRootPath("dump/"+id+"_heap.hprof");
        File file = new File(PathUtil.getRootPath("dump/"));
        if (!file.exists()){
            file.mkdirs();
        }
        CommandUtil.exeCommand("jmap -dump:format=b,file=" + path+" " +id);
        return path;
    }

    /**
     * 获取当前应用进程id
     * @return
     */
    public static String  getPid(){
        return String.valueOf(SigarSingleton.getSigarInstance().getPid());
    }
}
