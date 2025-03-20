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
    private Map<String, Channel> serverChannelMap = new ConcurrentHashMap<>();

    public Channel getChanelByIp(String ip){
        return serverChannelMap.getOrDefault(ip, null);
    }
    public void removeChanelByIp(String ip){
        serverChannelMap.remove(ip);
    }

    public void saveByAddr(String ip,Channel channel){
        serverChannelMap.put(ip,channel);
    }


    public ServerChannelManage(Map<String, Channel> serverChannelMap) {
        this.serverChannelMap = serverChannelMap;
    }

    public ServerChannelManage() {
    }

    public Map<String, Channel> getServerChannelMap() {
        return serverChannelMap;
    }

    public void setServerChannelMap(Map<String, Channel> serverChannelMap) {
        this.serverChannelMap = serverChannelMap;
    }
}
