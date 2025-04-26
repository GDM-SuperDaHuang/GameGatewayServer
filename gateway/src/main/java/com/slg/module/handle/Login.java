package com.slg.module.handle;

import com.google.protobuf.ByteString;
import com.slg.module.annotation.ToMethod;
import com.slg.module.annotation.ToServer;
import com.slg.module.connection.ClientChannelManage;
import com.slg.module.message.MsgResponse;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;


import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;

import static message.Login.*;
import static message.Account.*;
//用户登录
@ToServer
public class Login {
    @Autowired
    private ClientChannelManage clientchannelManage;
    @ToMethod(value = 3)
    public MsgResponse keyExchangeHandle(ChannelHandlerContext ctx, KeyExchangeReq request, long userId) throws IOException, InterruptedException {
        BigInteger g = new BigInteger(request.getG().toByteArray());
        BigInteger p = new BigInteger(request.getP().toByteArray());
        BigInteger clientPublicKey = new BigInteger(request.getPublicKey().toByteArray());
        // 1. 选择私钥 b (随机数，1 < b < p-1)
        SecureRandom random = new SecureRandom();
        BigInteger b;
        do {
            b = new BigInteger(p.bitLength() - 1, random);
        } while (b.compareTo(BigInteger.ONE) <= 0 || b.compareTo(p.subtract(BigInteger.ONE)) >= 0);
        // 2. 计算公钥 B = g^b mod p
        BigInteger B = g.modPow(b, p);
        // 3. 计算共享密钥 K = A^b mod p
        BigInteger K = clientPublicKey.modPow(b, p);

        ByteString serverPublicKey = ByteString.copyFrom(B.toByteArray());
        KeyExchangeResp.Builder builder = KeyExchangeResp.newBuilder()
                .setPublicKey(serverPublicKey);
        MsgResponse msgResponse = new MsgResponse();
        msgResponse.setBody(builder);
        msgResponse.setErrorCode(0);
        return msgResponse;
    }

    @ToMethod(value = 2)
    public MsgResponse loginHandle(ChannelHandlerContext ctx, LoginReq request, long userId) throws IOException, InterruptedException {
        clientchannelManage.put(ctx.channel(),122111L);

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
