package com.slg.module.rpc;

import com.slg.module.message.ByteBufferMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 目标服务器--网关
 */
public class TargetServerHandler extends SimpleChannelInboundHandler<ByteBufferMessage> {

    private final ChannelHandlerContext gatewayContext;//客户端--网关

    public TargetServerHandler(ChannelHandlerContext gatewayContext) {
        this.gatewayContext = gatewayContext;
    }


    /**
     * 接收目标服务器数据
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBufferMessage byteBufferMessage ) throws Exception {
        System.out.println("TargetServer received: " + byteBufferMessage);
        gatewayContext.writeAndFlush(byteBufferMessage);
        // ctx.close();
    }
}
