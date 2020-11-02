package com.lmnplace.commonutils.common.sigar;

import com.alibaba.fastjson.JSON;
import com.sun.management.OperatingSystemMXBean;
import org.hyperic.jni.ArchNotSupportedException;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import sun.management.ManagementFactoryHelper;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.util.concurrent.locks.ReentrantLock;

public class SigarSingleton {
   private static final Logger log= LoggerFactory.getLogger(SigarSingleton.class);
   private static Sigar sigar;
   private static final ReentrantLock lock=new ReentrantLock();
   static {
       initSigar();
   }
    //初始化sigar的配置文件
    private static void initSigar() {
        if(sigar!=null){
            return;
        }
        try {
            lock.lock();
            if (sigar == null) {
                doInitSigarSo();
                sigar=new Sigar();
            }
        }catch (Exception e){
            throw  new RuntimeException(e);
        }finally {
            lock.unlock();
        }
    }

    private static void doInitSigarSo() throws IOException, ArchNotSupportedException {
        SigarLoader loader = new SigarLoader(Sigar.class);
        String lib = loader.getLibraryName();
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource("classpath:/sigarso/" + lib);
        if (resource.exists()) {
            System.setProperty("org.hyperic.sigar.path", resource.getFile().getParent());
        }
    }
    public static Sigar getSigarInstance(){
        return sigar;
    }

    public static void main(String[] args) throws SigarException, ArchNotSupportedException, InterruptedException {
        OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactoryHelper.getOperatingSystemMXBean();
        System.out.println(JSON.toJSONString(osmxb));
        Thread.sleep(1000);
        osmxb = (OperatingSystemMXBean) ManagementFactory
                .getOperatingSystemMXBean();
        System.out.println(JSON.toJSONString(osmxb));
       /* SigarLoader loader = new SigarLoader(Sigar.class);
        String lib = loader.getLibraryName();
        InputStream in=SigarSingleton.class.getClass().getResourceAsStream("/sigarso/"+lib);*/
    }
}
