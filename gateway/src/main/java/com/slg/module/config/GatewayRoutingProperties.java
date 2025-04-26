package com.slg.module.config;

import com.slg.module.connection.ServerConfig;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Component
@ConfigurationProperties(prefix = "gateway.routing")  // 从配置读取
public class GatewayRoutingProperties {
    private List<ServerConfig> servers;
    private static Map<Integer,Byte> protoMap = new HashMap<>();
    @PostConstruct
    public  ServerConfig init() {
        for (ServerConfig server : servers) {
            byte serverId = server.getServerId();
            int protoIdMin = server.getProtoIdMin();
            int protoIdMax = server.getProtoIdMax();
        }

        if (protocolId>=protoIdMin&&protocolId=<protoIdMax){
            return ServerConfig;
        }
        return servers;
    }

    public  ServerConfig getServerByProtoId(int protocolId) {
        Byte b = protoMap.get(protocolId);
        if (protocolId>=protoIdMin&&protocolId=<protoIdMax){

        }

        return servers;
    }
}
