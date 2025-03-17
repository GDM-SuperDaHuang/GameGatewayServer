package com.slg.module.message;

import java.nio.ByteBuffer;

/**
 * 客户端与服务器之间协议
 */
public class ByteBufferMessage {
    private int cid;//顺序号
    private int errorCode;//错误码
    private int protocolId;//协议id
    private ByteBuffer body;//消息体

    public ByteBufferMessage() {
    }

    public ByteBufferMessage(int cid, int errorCode, int protocolId, ByteBuffer body) {
        this.cid = cid;
        this.errorCode = errorCode;
        this.protocolId = protocolId;
        this.body = body;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getProtocolId() {
        return protocolId;
    }

    public void setProtocolId(int protocolId) {
        this.protocolId = protocolId;
    }

    public ByteBuffer getBody() {
        return body;
    }

    public void setBody(ByteBuffer body) {
        this.body = body;
    }
}
