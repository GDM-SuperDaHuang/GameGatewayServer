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
    private final Map<Channel, Long> channelUserIdMap = new ConcurrentHashMap<>();//channel-userId
    private final Map<Long, Channel> userIdChannelMap = new ConcurrentHashMap<>();//userId-channel

    private final Map<Long, BigInteger> userIdCipherMap = new ConcurrentHashMap<>();//userId-共享密钥

    public ClientChannelManage() {
    }
    public void put(Channel channel, Long userId) {
        channelUserIdMap.put(channel, userId);
        userIdChannelMap.put(userId, channel);
    }

    public Long getUserId(Channel channel) {
        return channelUserIdMap.getOrDefault(channel, null);
    }

    public void putCipher(Long userId,BigInteger k) {
        userIdCipherMap.put(userId,k);
    }
    public BigInteger getCipher(Long userId) {
        userIdCipherMap.get(userId);
    }

    public Channel getChannelByUserId(Long userId) {
        return userIdChannelMap.getOrDefault(userId, null);
    }

    public void remove(Channel channel) {
        Long userId = channelUserIdMap.remove(channel);
        userIdChannelMap.remove(userId);
    }



}
