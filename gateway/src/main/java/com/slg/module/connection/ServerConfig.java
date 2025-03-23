package com.slg.module.connection;


public class ServerConfig {
    private int ServerId;
    private String ip;
    private int port;

    public ServerConfig() {
    }

    public ServerConfig(int serverId, String ip, int port) {
        ServerId = serverId;
        this.ip = ip;
        this.port = port;
    }

    public int getServerId() {
        return ServerId;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }
}
