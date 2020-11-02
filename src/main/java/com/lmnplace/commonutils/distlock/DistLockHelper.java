package com.lmnplace.commonutils.distlock;

import com.google.common.collect.Maps;
import com.lmnplace.commonutils.common.zk.ZkConf;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.RevocationListener;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class DistLockHelper {
    public static final String DISKLOCKBASEPATH = "/AI_DISTLOCK";
    private static Map<String,InterProcessMutex>  distLocks= Maps.newConcurrentMap();
    private static CuratorFramework distLockclient;
    private static InterProcessMutex instance(String lockName) {
        if (Objects.isNull(distLockclient)) {
            throw new RuntimeException("distLockclient is not inited");
        }
        InterProcessMutex lock= distLocks.get(lockName);
        if(!Objects.isNull(lock)){
            return lock;
        }
        if (DISKLOCKBASEPATH.equals(lockName)) {
            lock = new InterProcessMutex(distLockclient, String.format("%s", lockName));
        } else {
            lock = new InterProcessMutex(distLockclient, String.format("%s/%s", DISKLOCKBASEPATH, lockName));
        }

        distLocks.put(lockName,lock);
        return lock;
    }

    public static void acquire(String lockName) throws Exception {
        instance(lockName).acquire();
    }

    public static boolean acquire(String lockName,long time, TimeUnit unit) throws Exception {
        return instance(lockName).acquire(time, unit);
    }

    public static boolean isAcquiredInThisProcess(String lockName) {
        return instance(lockName).isAcquiredInThisProcess();
    }

    public static void init(ZkConf zkConf) {
        if (!Objects.isNull(distLockclient)) {
            return;
        }
        synchronized (DISKLOCKBASEPATH) {
            if (!Objects.isNull(distLockclient)) {
                return;
            }
            distLockclient = CuratorFrameworkFactory.newClient(zkConf.getServers(), new ExponentialBackoffRetry(1000, 3));
            distLockclient.start();
        }
    }

    public static void release(String lockName) throws Exception {
        instance(lockName).release();
    }

    public static Collection<String> getParticipantNodes(String lockName) throws Exception {
        return instance(lockName).getParticipantNodes();
    }

    public static void makeRevocable(String lockName,RevocationListener<InterProcessMutex> listener) {
        instance(lockName).makeRevocable(listener);
    }

    public static void makeRevocable(String lockName,RevocationListener<InterProcessMutex> listener, Executor executor) {
        instance(lockName).makeRevocable(listener, executor);
    }

    public static boolean isOwnedByCurrentThread(String lockName) {
        return instance(lockName).isOwnedByCurrentThread();
    }
}
