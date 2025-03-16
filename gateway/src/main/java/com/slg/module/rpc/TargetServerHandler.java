package com.slg.module.rpc;

import com.slg.module.connection.ClientChannel;
import com.slg.module.connection.ClientChannelManage;
import com.slg.module.message.ByteBufferServerMessage;
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
     * @param ctx   目标服务器-网关
     * @param msg 消息
     * @throws Exception .
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBufferServerMessage msg) throws Exception {

        System.out.println("TargetServer received: " + msg);
        long userId = msg.getUserId();
        int cid = msg.getCid();

        int protocolId = msg.getProtocolId();
        ByteBuffer byteBuffer = msg.getByteBuffer();

        //转发回给客户端
        ClientChannel clientChannel = channelManage.getChannelByUserId(userId);
        if (clientChannel!=null){
            Channel channel = clientChannel.getChannel();
            channel.writeAndFlush(msg);
        }else {//todo 没有，则让网关，返回错误码

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
