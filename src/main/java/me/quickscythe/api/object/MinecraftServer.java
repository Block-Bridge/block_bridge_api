package me.quickscythe.api.object;

import json2.JSONObject;

public class MinecraftServer {

    private String motd;
    private int onlinePlayers;
    private int maxPlayers;


    public MinecraftServer(JSONObject jsonObject) {
        this.motd = jsonObject.getString("motd");
        this.onlinePlayers = jsonObject.getInt("onlinePlayers");
        this.maxPlayers = jsonObject.getInt("maxPlayers");

    }

    public String getMotd() {
        return motd;
    }

    public int getOnlinePlayers() {
        return onlinePlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

}
