package com.lmnplace.commonutils.monitor.jvm.common;

import com.alibaba.fastjson.JSON;
import com.lmnplace.commonutils.common.sigar.SigarSingleton;
import com.lmnplace.commonutils.monitor.jvm.model.JinfoModel;
import com.lmnplace.commonutils.utils.ArrayUtil;
import com.lmnplace.commonutils.utils.CmdResult;
import com.lmnplace.commonutils.utils.CommandUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;
public class Jinfo {


    /**
     * JVM默认参数与指定参数
     * @return
     */
    public static  JinfoModel info() throws IOException {
        CmdResult s =  CommandUtil.exeCommand( "jinfo -flags"+getPid());
        if (!s.isSuccess()){
            return null;
        }
        String flags = "flags:";
        String command = "Command line:";
        //默认参数
        String[] noedefault = ArrayUtil.trim(s.getMsg().substring(s.getMsg().indexOf(flags)+flags.length(),s.getMsg().indexOf(command)).split("\\s+"));
        String[] commandLine = null;
        String cmd = s.getMsg().substring(s.getMsg().indexOf(command));
        if (!cmd.equals(command)){
            commandLine = s.getMsg().substring(command.length()).split("\\s+");
        }
        commandLine = ArrayUtil.trim(commandLine);
        return new JinfoModel(Arrays.stream(noedefault).collect(Collectors.toList()), Arrays.stream(commandLine).collect(Collectors.toList()) );
    }


    /**
     * 获取当前应用进程id
     * @return
     */
    public static String  getPid(){
        return String.valueOf(SigarSingleton.getSigarInstance().getPid());
    }

    public static void main(String[] args) throws IOException {
        System.out.println(JSON.toJSONString(Jinfo.info()));
    }
}
