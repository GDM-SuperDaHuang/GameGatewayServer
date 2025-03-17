package com.slg.module.rpc.interMsg;

import com.slg.module.message.ByteBufferServerMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.ByteBuffer;


/**
 * 出
 */
public class MsgServerInterEncode extends MessageToByteEncoder<ByteBufferServerMessage> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBufferServerMessage msg, ByteBuf out) throws Exception {

        // 写入消息头
        out.writeLong(msg.getUserId());      // 8字节
        out.writeInt(msg.getCid());
        out.writeInt(msg.getErrorCode());
        out.writeInt(msg.getProtocolId());      // 4字节
        out.writeByte(0);                       // zip压缩标志，1字节
        out.writeByte(3);                       // pb版本，1字节

        // 获取消息体长度并写入
        ByteBuffer body = msg.getBody();
        int length = body.remaining();
        out.writeShort(length);                 // 消息体长度，2字节

        // 写入消息体
        out.writeBytes(body);

    }
}