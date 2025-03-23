package com.slg.module.rpc.interMsg;

import com.slg.module.connection.ClientChannelManage;
import com.slg.module.connection.ServerChannelManage;
import com.slg.module.message.ByteBufferServerMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.DecoderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.net.SocketAddress;
import java.net.SocketException;

/**
 * 目标服务器--网关
 */
@Component
@ChannelHandler.Sharable
public class TargetServerHandler extends SimpleChannelInboundHandler<ByteBufferServerMessage> {
    @Autowired
    private ClientChannelManage channelManage;
    //目标服务器--网关管理
    @Autowired
    private ServerChannelManage serverChannelManage;
    /**
     * 接收目标服务器数据
     *
     * @param ctx 目标服务器-网关
     * @param msg 消息
     * @throws Exception .
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBufferServerMessage msg) throws Exception {
        long userId = msg.getUserId();
        byte[] body = msg.getBody();
        //转发回给客户端
        Channel clientChannel = channelManage.getChannelByUserId(userId);
        if (clientChannel != null) {
            ByteBuf out = buildClientMsg(msg.getCid(), msg.getErrorCode(), msg.getProtocolId(), 0, 1, body);
            clientChannel.writeAndFlush(out)
                    .addListener(future -> {
                        if (future.isSuccess()) {

                        } else {

                            System.err.println("Write and flush failed: " + future.cause());
                        }
                    });

        } else {//todo 没有，则让网关，返回错误码
        }
    }

    public ByteBuf buildClientMsg(int cid, int errorCode, int protocolId, int zip, int version, byte[] bodyArray) {
        int length = bodyArray.length;
        //写回
        ByteBuf out = Unpooled.buffer(16 + length);
        //消息头
        out.writeInt(cid);      // 4字节
        out.writeInt(errorCode);      // 4字节
        out.writeInt(protocolId);      // 4字节
        out.writeByte(zip);                       // zip压缩标志，1字节
        out.writeByte(version);                       // pb版本，1字节
        //消息体
        out.writeShort(bodyArray.length);                 // 消息体长度，2字节
        // 写入消息体
        out.writeBytes(bodyArray);
        return out;
    }

    /**
     * 网关-服务器错误处理
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof InvocationTargetException) {
            //目标方法错误
        } else if (cause instanceof SocketException
                || cause instanceof DecoderException) {
            destroyConnection(ctx);
            //客户端关闭连接/连接错误
            // 关闭连接
            ctx.close();
        }
    }

    /**
     * 关闭内部服务器连接
     */
    private void destroyConnection(ChannelHandlerContext ctx) {
        //断开无效连接
        SocketAddress socketAddress = ctx.channel().remoteAddress();

        String addr = socketAddress.toString();

        Channel channel = serverChannelManage.removeChanelByIp(addr);
        System.out.println("内部服务器关闭连接："+channel+"地址："+addr);
    }
}
