package com.slg.module.rpc;

import com.slg.module.message.ByteBufferMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class TargetServerHandler extends SimpleChannelInboundHandler<ByteBufferMessage> {
    private final ChannelHandlerContext gatewayContext;

    public TargetServerHandler(ChannelHandlerContext gatewayContext) {
        this.gatewayContext = gatewayContext;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBufferMessage byteBufferMessage ) throws Exception {
        System.out.println("TargetServer received: " + byteBufferMessage);
        // 模拟处理逻辑
        String response = "Processed: " + byteBufferMessage;
        // 将结果返回给网关
        gatewayContext.writeAndFlush(response);
        ctx.close();
    }
}
