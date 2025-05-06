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

    private final Map<String, BigInteger> ipCipherMap = new ConcurrentHashMap<>();//ipInfo-key共享密钥
    private final Map<Long, BigInteger> userIdCipherMap = new ConcurrentHashMap<>();//ipInfo-key共享密钥

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

    public void putCipher(String ip,BigInteger k) {
        ipCipherMap.put(ip,k);
    }
    public BigInteger getCipher(String ip) {
        ipCipherMap.get(ip);
    }

    public Channel getChannelByUserId(Long userId) {
        return userIdChannelMap.getOrDefault(userId, null);
    }

    public void remove(Channel channel) {
        Long userId = channelUserIdMap.remove(channel);
        userIdChannelMap.remove(userId);
    }



}
