package com.lmnplace.commonutils.monitor.jvm.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

/**
 * Create by yster@foxmail.com 2018/11/15 0015 0:23
 */
public class Javav {
    private static final Logger log= LoggerFactory.getLogger(Javav.class);
    public static String version() {
        BufferedReader bufferedReader=null;
        InputStreamReader inputStreamReader = null;
        Process p = null;
        try{
            p = Runtime.getRuntime().exec(new String[]{"java", "-version"});
            inputStreamReader = new InputStreamReader(p.getErrorStream());
            bufferedReader = new BufferedReader(inputStreamReader);
            String line="";
            while ((line = bufferedReader.readLine()) != null) {
                break;
            }
            String[] vs=line.split(" ");
            return vs.length>0?vs[vs.length-1].replace("\"",""):"";
        } catch (IOException e) {
            log.error("To get jdk'version is fail",e);
        }finally {
            if(!Objects.isNull(bufferedReader)){
                try {
                    bufferedReader.close();
                }catch (Exception e){}
            }
            if(!Objects.isNull(inputStreamReader)){
                try {
                    inputStreamReader.close();
                }catch (Exception e){}
            }
            if(!Objects.isNull(p)){
                try {
                    p.destroy();
                }catch (Exception e){}
            }
        }
        return "";
    }




}
