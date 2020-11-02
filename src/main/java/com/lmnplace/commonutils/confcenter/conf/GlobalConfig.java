package com.lmnplace.commonutils.confcenter.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "confcenter.global.share")
@EnableConfigurationProperties
@Component
public class GlobalConfig {
    public static final String DISCARD_DATA_LOG_ENABLE_KEY = "cconfcenter.global.share.enableDiscardDatalog";
    public static final String EX_LOG_ENABLE_KEY = "confcenter.global.share.enableExLog";
    public static final String TRACK_DATA_LOG_ENABLE_KEY = "confcenter.global.share.enableTackDataLog";
    private boolean enableDiscardDataLog;
    private boolean enableTrackDataLog;
    private boolean enableExLog;

    public boolean isEnableDiscardDataLog() {
        return enableDiscardDataLog;
    }

    public void setEnableDiscardDataLog(boolean enableDiscardDataLog) {
        this.enableDiscardDataLog = enableDiscardDataLog;
    }

    public boolean isEnableTrackDataLog() {
        return enableTrackDataLog;
    }

    public void setEnableTrackDataLog(boolean enableTrackDataLog) {
        this.enableTrackDataLog = enableTrackDataLog;
    }

    public boolean isEnableExLog() {
        return enableExLog;
    }

    public void setEnableExLog(boolean enableExLog) {
        this.enableExLog = enableExLog;
    }
}
