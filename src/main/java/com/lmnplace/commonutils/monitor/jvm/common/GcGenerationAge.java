package com.lmnplace.commonutils.monitor.jvm.common;

import java.util.HashMap;
import java.util.Map;

public enum GcGenerationAge {
    OLD,
    YOUNG,
    UNKNOWN;

    private static Map<String, GcGenerationAge> knownCollectors = new HashMap<String, GcGenerationAge>() {
        {
            this.put("ConcurrentMarkSweep", GcGenerationAge.OLD);
            this.put("Copy", GcGenerationAge.YOUNG);
            this.put("G1 Old Generation", GcGenerationAge.OLD);
            this.put("G1 Young Generation", GcGenerationAge.YOUNG);
            this.put("MarkSweepCompact", GcGenerationAge.OLD);
            this.put("PS MarkSweep", GcGenerationAge.OLD);
            this.put("PS Scavenge", GcGenerationAge.YOUNG);
            this.put("ParNew", GcGenerationAge.YOUNG);
        }
    };

    private GcGenerationAge() {
    }

   public static GcGenerationAge fromName(String name) {
        GcGenerationAge t = (GcGenerationAge)knownCollectors.get(name);
        return t == null ? UNKNOWN : t;
    }
}