package com.slg.module.rpc;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IdleStateEventHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.WRITER_IDLE) {
                // 发送心跳包
                ctx.writeAndFlush(new ByteBufferMessage(0, 0, "PING".getBytes()));
                log.debug("Send heartbeat to target server: {}", ctx.channel().remoteAddress());
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}