package com.lmnplace.commonutils.common;

public enum MessageType {
    MT_JVMPRO, //JVM监控信息
    MT_SYS,//sys监控信息
    MT_ZK,//zk监控信息
    MT_KAFKA,//kafka监控信息
    MT_DB,//DB监控信息
    BZ_DISCARDATA,//数据屏蔽信息
    BZ_TRACKDATA,//数据跟踪信息
    BZ_EX //异常信息
    ;
    private MessageType(){
    }
}
