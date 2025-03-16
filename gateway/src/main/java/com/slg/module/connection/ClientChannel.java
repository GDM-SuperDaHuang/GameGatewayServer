package com.slg.module.connection;

import io.netty.channel.Channel;
import org.springframework.stereotype.Component;

public class ClientChannel {
    private Channel channel;
    private String token;
    private String addr;
    private Long userId;

    public ClientChannel() {
    }

    public ClientChannel(Channel channel, String token, String addr, Long userId) {
        this.channel = channel;
        this.token = token;
        this.addr = addr;
        this.userId = userId;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
