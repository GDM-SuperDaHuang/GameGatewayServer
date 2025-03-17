package com.slg.module.rpc;


import com.slg.module.message.ByteBufferServerMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;


@ChannelHandler.Sharable
public class IdleStateEventHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.WRITER_IDLE) {
                // 发送心跳包
                //todo
                ctx.writeAndFlush(new ByteBufferServerMessage(0,0, 0, 0,null));
//                log.debug("Send heartbeat to target server: {}", ctx.channel().remoteAddress());
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}