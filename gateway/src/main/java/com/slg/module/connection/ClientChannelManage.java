package com.slg.module.connection;

import io.netty.channel.Channel;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 客户端连接管理
 */
@Component
public class ClientChannelManage {
    //客户端连接管理
    private final Map<Channel, Long> channelUserIdMap = new ConcurrentHashMap<>();
    private final Map<Long, Channel> userIdChannelMap = new ConcurrentHashMap<>();
    private final Map<String, Channel> addrChannelMap = new ConcurrentHashMap<>();

    public void put1(Channel channel, Long userId) {
        channelUserIdMap.put(channel, userId);
    }

    public Long get1(Channel channel) {
        return channelUserIdMap.get(channel);
    }

    public void put(String addr, Channel channel) {
        addrChannelMap.put(addr, channel);
    }
    public Channel get(String addr) {
        return addrChannelMap.get(addr);
    }

    public ClientChannelManage() {
    }

    public void put(Channel channel, Long userId) {
        channelUserIdMap.put(channel, userId);
        userIdChannelMap.put(userId, channel);
    }

    public Long getUserId(Channel channel) {
        return channelUserIdMap.getOrDefault(channel, null);
    }

    public Channel getChannelByUserId(Long userId) {
        return userIdChannelMap.getOrDefault(userId, null);
    }

    public void remove(Channel channel) {
        Long userId = channelUserIdMap.remove(channel);
        userIdChannelMap.remove(userId);
    }

}
