package com.slg.module.handle;

import com.slg.module.annotation.ToMethod;
import com.slg.module.annotation.ToServer;
import com.slg.module.interfaceT.monitor1.EventPublisher;
import com.slg.module.message.MSG;
import com.slg.module.message.SendMsg;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
@ToServer
public class Test {
    private static final Logger logger = LogManager.getLogger(Test.class);
    private int sum = 0;
    private SendMsg sendMsg;
    @Autowired
    EventPublisher publisher;
    public Test(SendMsg sendMsg) {
        this.sendMsg = sendMsg;
    }

    @ToMethod(value = 1)
    public void diy(ChannelHandlerContext ctx, MSG.LoginRequest request,long userId) throws IOException {
        sum++;
        logger.debug("debug request:{}",request);
        logger.info("info request:{}",request);
        logger.warn("warn request:{}",request);
        logger.error("error request:{}",request);
        MSG.LoginResponse.Builder builder = MSG.LoginResponse.newBuilder();
        byte[] byteArray = MSG.LoginResponse.newBuilder()
                .setAaa(1111111111)
                .setBbb(2132123132)
                .buildPartial()
                .toByteArray();

        MSG.LoginResponse.Builder LoginResponseBuilder = MSG.LoginResponse.newBuilder()
                .setAaa(999999999)
                .setBbb(777777777);
        sendMsg.send(ctx, LoginResponseBuilder);

        sendMsg.send(ctx, byteArray);
//        publisher.publishCustomEvent(LoginResponseBuilder);
        if (sum > 9003) {
            System.out.println("服务器收到数据sum" + sum);
        }
        if (sum == 70000) {
            System.out.println("服务器收到数据sum" + sum);
        }
        if (sum == 80000) {
            System.out.println("服务器收到数据sum" + sum);
        }
        if (sum == 85000) {
            System.out.println("服务器收到数据sum" + sum);
        }
        if (sum == 90000) {
            System.out.println("服务器收到数据sum" + sum);
        }
        if (sum >= 99900) {
            System.out.println("服务器收到数据sum" + sum);
        }
    }


    @ToMethod(value = 2)
    public void ffff(ChannelHandlerContext ctx, MSG.FriendRequest request,long userId) throws IOException {
        byte[] byteArray = MSG.LoginResponse.newBuilder()
                .setAaa(1111111111)
                .setBbb(2132123132)
                .buildPartial()
                .toByteArray();

        MSG.LoginResponse.Builder LoginResponseBuilder = MSG.LoginResponse.newBuilder()
                .setAaa(999999999)
                .setBbb(777777777);
        sendMsg.send(ctx, LoginResponseBuilder);
        sendMsg.send(ctx, byteArray);
    }

}