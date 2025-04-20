package com.slg.module.handle;

import com.slg.module.annotation.ToMethod;
import com.slg.module.annotation.ToServer;
import com.slg.module.message.MsgResponse;
import io.netty.channel.ChannelHandlerContext;
import java.io.IOException;
import static message.Login.*;

//用户登录
@ToServer
public class Login {

    @ToMethod(value = 1)
    public MsgResponse loginHandle(ChannelHandlerContext ctx, LoginReq request, long userId) throws IOException, InterruptedException {


        LoginResponse.Builder builder = LoginResponse.newBuilder()
                .setAaa(999999999)
                .setBbb(777777777);
//        MSG.LoginRequest.parseFrom()
        MsgResponse msgResponse = new MsgResponse();
        msgResponse.setBody(builder);
        msgResponse.setErrorCode(0);
        return msgResponse;
    }
}
