package com.slg.module.rpc.outsideMsg;

import com.slg.module.message.ByteBufferMessage;
import com.slg.module.message.MsgResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MsgEncode extends MessageToByteEncoder<MsgResponse> {
    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBufferMessage msg, ByteBuf out) throws Exception {
        // 写入消息头
        out.writeLong(msg.getCid());      // 4字节
        out.writeLong(msg.getErrorCode());      // 4字节
        out.writeInt(msg.getProtocolId());      // 4字节
        out.writeByte(0);                       // zip压缩标志，1字节
        out.writeByte(1);                       // pb版本，1字节
        // 获取消息体长度并写入
        int length = msg.getByteBuffer().remaining();
        out.writeShort(length);                 // 消息体长度，2字节

        // 写入消息体
        out.writeBytes(msg.getByteBuffer());
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, MsgResponse msgResponse, ByteBuf byteBuf) throws Exception {

    }
}