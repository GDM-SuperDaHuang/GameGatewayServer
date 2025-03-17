package com.slg.module.handle;

import com.slg.module.annotation.ToMethod;
import com.slg.module.annotation.ToServer;


import com.slg.module.message.MSG;
//import com.slg.module.message.MsgResponse;
import com.slg.module.message.MsgResponse;
import com.slg.module.message.SendMsg;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@ToServer
public class Test {
    private static final Logger logger = LogManager.getLogger(Test.class);
    private SendMsg sendMsg;

    public Test(SendMsg sendMsg) {
        this.sendMsg = sendMsg;
    }

    @ToMethod(value = 1)
    public MsgResponse diy(ChannelHandlerContext ctx, MSG.LoginRequest request, long userId) throws IOException, InterruptedException {
        MSG.LoginResponse.Builder builder = MSG.LoginResponse.newBuilder()
                .setAaa(999999999)
                .setBbb(777777777);
        MsgResponse msgResponse = new MsgResponse();
        msgResponse.setBody(builder);
        msgResponse.setErrorCode(0);
        return msgResponse;
    }


    @ToMethod(value = 2)
    public MsgResponse ffff(ChannelHandlerContext ctx, MSG.FriendRequest request,long userId) throws IOException {
        ArrayList<Long> longs = new ArrayList<>();
        longs.add(110L);
        longs.add(211L);
        MSG.FriendsResponse.Builder friendsResponse = MSG.FriendsResponse.newBuilder()
                .addAllUserIdList(longs);
        MsgResponse msgResponse = new MsgResponse();
        msgResponse.setBody(friendsResponse);
        msgResponse.setErrorCode(0);
        return msgResponse;
    }

}