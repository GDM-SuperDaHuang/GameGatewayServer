package com.slg.module.message;

import java.nio.ByteBuffer;

/**
 * 服务器内部协议
 */
public class ByteBufferServerMessage {
    private long userId;
    private int cid;//顺序号
    private int errorCode;//错误码
    private int protocolId;
    private ByteBuffer byteBuffer;

    public ByteBufferServerMessage() {
    }

    public ByteBufferServerMessage(long userId, int cid, int errorCode, int protocolId, ByteBuffer byteBuffer) {
        this.userId = userId;
        this.cid = cid;
        this.errorCode = errorCode;
        this.protocolId = protocolId;
        this.byteBuffer = byteBuffer;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
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

    public ByteBuffer getByteBuffer() {
        return byteBuffer;
    }

    public void setByteBuffer(ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
    }
}
