package com.slg.module.handle;

import com.google.protobuf.ByteString;
import com.slg.module.annotation.ToMethod;
import com.slg.module.annotation.ToServer;
import com.slg.module.connection.ClientChannelManage;
import com.slg.module.message.MsgResponse;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;


import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
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

    // 密钥交换
    @ToMethod(value = 3)
    public MsgResponse keyExchangeHandle(ChannelHandlerContext ctx, KeyExchangeReq request, Long userId) throws IOException, InterruptedException {
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
        return msgResponse;
    }

    // 密钥验证
    @ToMethod(value = 4)
    public MsgResponse keyExchangeHandle2(ChannelHandlerContext ctx, KeyExchangeReq request, Long userId) throws IOException, InterruptedException {
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
        return msgResponse;
    }


    @ToMethod(value = 1)
    public MsgResponse loginHandle(ChannelHandlerContext ctx, LoginReq request, Long userId) throws IOException, InterruptedException {
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


        // 密钥派生函数
    private SecretKey deriveKey(BigInteger sharedKey) throws Exception {
        String password = sharedKey.toString();
        String salt = "randomSalt";
        int iterations = 65536;
        int keyLength = 256;
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), iterations, keyLength);
        byte[] derivedKeyBytes = skf.generateSecret(spec).getEncoded();
        return new SecretKeySpec(derivedKeyBytes, "AES");
    }

    // 加密函数
    private byte[] encrypt(String plaintext, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(plaintext.getBytes());
    }

    // 字节数组转十六进制字符串
    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}
