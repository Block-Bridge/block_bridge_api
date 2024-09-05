package me.quickscythe.api.object;

import json2.JSONObject;

import java.sql.ResultSet;

public class MinecraftServer {

    private final String name;
    private final String ip;
    private final int port;
    private final String motd;
    private final int onlinePlayers;
    private final int maxPlayers;


    public MinecraftServer(JSONObject jsonObject) {
        this.name = jsonObject.getString("name");
        this.ip = jsonObject.getString("ip");
        this.port = jsonObject.getInt("port");
        this.motd = jsonObject.getString("motd");
        this.onlinePlayers = jsonObject.getInt("onlinePlayers");
        this.maxPlayers = jsonObject.getInt("maxPlayers");

    }

//    public MinecraftServer(ResultSet rs) {
//
//        try {
//            this.name = rs.getString("name");
//            this.ip = rs.getString("ip");
//            this.port = rs.getInt("port");
//            this.motd = rs.getString("motd");
//            this.onlinePlayers = rs.getInt("onlinePlayers");
//            this.maxPlayers = rs.getInt("maxPlayers");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public String getMotd() {
        return motd;
    }

    public int getOnlinePlayers() {
        return onlinePlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public String getName() {
        return name;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String getServerAddress() {
        return ip + ":" + port;
    }


}
