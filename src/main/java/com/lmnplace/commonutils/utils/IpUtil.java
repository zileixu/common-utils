package com.lmnplace.commonutils.utils;

import com.lmnplace.commonutils.common.constants.BaseConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class IpUtil {
    private static final Logger LOG=LoggerFactory.getLogger(IpUtil.class);
    private static final String DEFAULTIP = "127.0.0.1";
    private static String CURRENTHOSTIP = null;

    public static String getHostIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
        }
        return DEFAULTIP;
    }

    public static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
        }
        return "未知";
    }
    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader(BaseConstant.XFORWARDEDFOR);
        if (ip == null || ip.length() == BaseConstant.CONST0 || BaseConstant.UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader(BaseConstant.PROXYCLIENTIP);
        }
        if (ip == null || ip.length() == BaseConstant.CONST0 || BaseConstant.UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader(BaseConstant.WLPROXYCLIENTIP);
        }
        if (ip == null || ip.length() == BaseConstant.CONST0 || BaseConstant.UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader(BaseConstant.HTTPCLIENTIP);
        }
        if (ip == null || ip.length() == BaseConstant.CONST0 || BaseConstant.UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader(BaseConstant.HTTPXFORWARDFOR);
        }
        if (ip == null || ip.length() == BaseConstant.CONST0 || BaseConstant.UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 如果是多级代理，那么取第一个ip为客户端ip
        if (ip != null && ip.indexOf(BaseConstant.COMMA) != BaseConstant.CONST_1) {
            ip = ip.substring(BaseConstant.CONST0, ip.indexOf(BaseConstant.COMMA)).trim();
        }

        return ip;
    }

    public static String getServerIpAddress() {
        if (CURRENTHOSTIP == null) {
            synchronized (DEFAULTIP) {
                if (CURRENTHOSTIP == null) {
                    try {
                        Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
                        while (allNetInterfaces.hasMoreElements()) {
                            NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                            Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                            while (addresses.hasMoreElements()) {
                                InetAddress ip = (InetAddress) addresses.nextElement();
                                if (ip != null
                                        && ip instanceof Inet4Address
                                        && !ip.isLoopbackAddress() //loopback地址即本机地址，IPv4的loopback范围是127.0.0.0 ~
                                        // 127.255.255.255
                                        && ip.getHostAddress().indexOf(BaseConstant.COLON) == -1) {
                                    CURRENTHOSTIP = ip.getHostAddress();
                                    break;
                                }
                            }
                        }
                    } catch (Exception e) {
                        CURRENTHOSTIP = DEFAULTIP;
                        LOG.error("It's fail to get the IP of local server,default ip :{}",CURRENTHOSTIP,e);
                    }
                    if (CURRENTHOSTIP == null) {
                        CURRENTHOSTIP = DEFAULTIP;
                        LOG.error("It's fail ,because haven't valid ip ,default ip :{}",CURRENTHOSTIP);
                    }
                    return CURRENTHOSTIP;
                }
                else {
                    return CURRENTHOSTIP;
                }
            }
        }
        else {
            return CURRENTHOSTIP;
        }
    }

    public static void main(String[] args) {
        System.out.println(getServerIpAddress());
    }
}
