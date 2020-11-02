package com.lmnplace.commonutils.registcenter.conf;

import com.lmnplace.commonutils.registcenter.mode.ServiceNode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "registcenter.servicenode")
@EnableConfigurationProperties
@Component
public class NodeConf extends ServiceNode {
}
