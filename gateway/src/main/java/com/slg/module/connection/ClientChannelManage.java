package com.slg.module.connection;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ClientChannelManage {
    //客户端连接管理
    private final Map<String, ClientChannel> AddrChannelMap = new ConcurrentHashMap<>();
    private final Map<Long, ClientChannel> userIdChannelMap = new ConcurrentHashMap<>();

    public ClientChannelManage() {
    }

    public void saveChannelByAddr(String addr,Long userId,ClientChannel channel){
        AddrChannelMap.put(addr,channel);
        userIdChannelMap.put(userId,channel);
    }

    public ClientChannel getChannelByAddr(String addr){
        return AddrChannelMap.getOrDefault(addr,null);
    }

    public ClientChannel getChannelByUserId(Long userId){
        return userIdChannelMap.getOrDefault(userId,null);
    }

    public void removeByUserId(String addr){
        ClientChannel clientChannel = AddrChannelMap.get(addr);
        Long userId = clientChannel.getUserId();
        userIdChannelMap.remove(userId);
        AddrChannelMap.remove(addr);
    }

}
