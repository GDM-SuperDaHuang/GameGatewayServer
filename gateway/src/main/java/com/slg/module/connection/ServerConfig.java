package com.slg.module.connection;


public class ServerConfig {
    private byte serverId;
    private byte groupId;
    private String ip;
    private int port;
    private int protoIdMin;
    private int protoIdMax;

    public ServerConfig(byte serverId, String ip, int port) {
        this.serverId = serverId;
        this.ip = ip;
        this.port = port;
    }

    public ServerConfig() {
    }

    public ServerConfig(String ip, int port, byte groupId, byte serverId, int protoIdMin, int protoIdMax) {
        this.ip = ip;
        this.port = port;
        this.groupId = groupId;
        this.serverId = serverId;
        this.protoIdMin = protoIdMin;
        this.protoIdMax = protoIdMax;
    }


    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public byte getGroupId() {
        return groupId;
    }

    public void setGroupId(byte groupId) {
        this.groupId = groupId;
    }

    public byte getServerId() {
        return serverId;
    }

    public void setServerId(byte serverId) {
        this.serverId = serverId;
    }

    public int getProtoIdMin() {
        return protoIdMin;
    }

    public void setProtoIdMin(int protoIdMin) {
        this.protoIdMin = protoIdMin;
    }

    public int getProtoIdMax() {
        return protoIdMax;
    }

    public void setProtoIdMax(int protoIdMax) {
        this.protoIdMax = protoIdMax;
    }
}
