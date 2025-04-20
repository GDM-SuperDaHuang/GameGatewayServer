package com.slg.module.config;

import com.slg.module.connection.ServerConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;
@Component
@ConfigurationProperties(prefix = "gateway.routing")  // 从配置读取
public class GatewayRoutingProperties {
    private Map<Integer, ServerConfig> servers;
    public void setServers(Map<Integer, ServerConfig> servers) {
        this.servers = servers;
    }

    public Map<Integer, ServerConfig> getServers() {
        return servers;
    }
}
