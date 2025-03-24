package com.slg.module.connection;

import io.netty.channel.Channel;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 目标服务器内部消息管理
 */
@Component
public class ServerChannelManage {
    //目标服务器连接管理
    private Map<Integer, Channel> serverChannelMap = new ConcurrentHashMap<>();//<serverId,channel>
    private Map<Integer, ServerConfig> serverConfigMap = new ConcurrentHashMap<>();////<serverId,ServerConfig>

    public Channel getChanelByIp(Integer serverId) {
        return serverChannelMap.getOrDefault(serverId, null);
    }

    public Channel removeChanelByIp(Integer serverId) {
        Channel remove = serverChannelMap.remove(serverId);
        serverConfigMap.remove(serverId);
        return remove;
    }

    public void put(Integer serverId, Channel channel, ServerConfig serverConfig) {
        serverChannelMap.put(serverId, channel);
        serverConfigMap.put(serverId, new ServerConfig(serverId, serverConfig.getIp(), serverConfig.getPort()));
    }

    public ServerChannelManage() {
    }

    public Map<Integer, ServerConfig> getServerConfigMap() {
        return serverConfigMap;
    }
}
