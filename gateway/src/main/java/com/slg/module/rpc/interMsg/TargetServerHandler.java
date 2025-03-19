package com.slg.module.rpc.interMsg;

import com.slg.module.connection.ClientChannel;
import com.slg.module.connection.ClientChannelManage;

import com.slg.module.message.ByteBufferMessage;
import com.slg.module.message.ByteBufferServerMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.DecoderException;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationTargetException;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Map;

/**
 * 目标服务器--网关
 */
public class TargetServerHandler extends SimpleChannelInboundHandler<ByteBufferServerMessage> {

//    private final ChannelHandlerContext gatewayContext;//客户端--网关
//
//    public TargetServerHandler(ChannelHandlerContext gatewayContext) {
//        this.gatewayContext = gatewayContext;
//    }

    @Autowired
    private ClientChannelManage channelManage;


    /**
     * 接收目标服务器数据
     *
     * @param ctx 目标服务器-网关
     * @param msg 消息
     * @throws Exception .
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBufferServerMessage msg) throws Exception {

        System.out.println("TargetServer received: " + msg);
        long userId = msg.getUserId();
        int cid = msg.getCid();

        int protocolId = msg.getProtocolId();
        ByteBuffer body = msg.getBody();

//        ByteBuffer byteBuffer = msg.getByteBuffer();

        //转发回给客户端
        ClientChannel clientChannel = channelManage.getChannelByUserId(userId);
        if (clientChannel != null) {
            //写回
            ByteBuf out = Unpooled.buffer(16);
            //消息头
            out.writeInt(msg.getCid());      // 4字节
            out.writeInt(msg.getErrorCode());      // 4字节
            out.writeInt(msg.getProtocolId());      // 4字节
            out.writeByte(0);                       // zip压缩标志，1字节
            out.writeByte(1);                       // pb版本，1字节
            byte[] bodyArray = body.array();
            out.writeShort(bodyArray.length);  // 消息体长度，2字节
            // 写入消息体
            out.writeBytes(bodyArray);

            Channel channel = clientChannel.getChannel();
            channel.writeAndFlush(out)
                    .addListener(future -> {
                        if (future.isSuccess()) {
                            // 操作成功，释放 ByteBuf
                            out.release();
                        } else {
                            // 操作失败，也释放 ByteBuf
                            out.release();
                            System.err.println("Write and flush failed: " + future.cause());
                        }
                    });
        } else {//todo 没有，则让网关，返回错误码

        }

    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof InvocationTargetException) {
            //目标方法错误
        } else if (cause instanceof SocketException
                || cause instanceof DecoderException) {
            //客户端关闭连接/连接错误
            // 关闭连接
            ctx.close();
        }
    }
}
