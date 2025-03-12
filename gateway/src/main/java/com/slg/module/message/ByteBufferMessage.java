package com.slg.module.message;

import java.nio.ByteBuffer;

public class ByteBufferMessage {
    private long sessionId;
    private int protocolId;
    private ByteBuffer byteBuffer;
    
    public ByteBufferMessage() {
    }

    public ByteBufferMessage(long sessionId, int protocolId, ByteBuffer byteBuffer) {
        this.sessionId = sessionId;
        this.protocolId = protocolId;
        this.byteBuffer = byteBuffer;
    }

    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
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
