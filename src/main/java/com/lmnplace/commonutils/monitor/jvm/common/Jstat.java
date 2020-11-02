package com.lmnplace.commonutils.monitor.jvm.common;

import com.lmnplace.commonutils.common.sigar.SigarSingleton;
import com.lmnplace.commonutils.monitor.jvm.model.KVModel;
import com.lmnplace.commonutils.utils.ArrayUtil;
import com.lmnplace.commonutils.utils.CmdResult;
import com.lmnplace.commonutils.utils.CommandUtil;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
public class Jstat {

    /**
     * Jstat 模板方法
     *
     * @param cmd 命令
     * @return 集合
     */
    private static List<KVModel> jstat(String cmd) throws Exception {
        List<KVModel> list = new ArrayList<>();
        CmdResult s = CommandUtil.exeCommand(cmd);
        assert s != null;
        BufferedReader reader = new BufferedReader(new StringReader(s.getMsg()));
        String[] keys = ArrayUtil.trim(reader.readLine().split("\\s+|\t"));
        String[] values = ArrayUtil.trim(reader.readLine().split("\\s+|\t"));
        // 特殊情况
        if (cmd.contains("-compiler")) {
            for (int i = 0; i < 4; i++) {
                list.add(new KVModel(keys[i], values[i]));
            }
            return list;
        }
        // 正常流程
        for (int i = 0; i < keys.length; i++) {
            list.add(new KVModel(keys[i], values[i]));
        }
        return list;
    }

    /**
     * 类加载信息
     * X轴为时间，Y轴为值的变化
     * @return
     */
    public static List<KVModel> jstatClass() throws Exception {
        String id=getPid();
        List<KVModel> jstatClass = jstat("jstat -class " + id);
        List<KVModel> jstatCompiler = jstat("jstat -compiler" + id);
        jstatClass.addAll(jstatCompiler);
        return jstatClass;
    }

    /**
     * 堆内存信息
     * X轴为时间，Y轴为值的变化
     * @return
     */
    public static List<KVModel> jstatGc() throws Exception {
        String id=getPid();
        return jstat("jstat -gc" + id);
    }

    /**
     * 堆内存百分比
     * 实时监控
     * @return
     */
    public static List<KVModel> jstatUtil() throws Exception {
        String id=getPid();
        return jstat("jstat -gcutil"+ id);
    }


    /**
     * 获取当前应用进程id
     * @return
     */
    public static String  getPid(){
        return String.valueOf(SigarSingleton.getSigarInstance().getPid());
    }
}
