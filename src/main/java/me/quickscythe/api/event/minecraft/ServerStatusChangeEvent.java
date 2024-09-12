package me.quickscythe.api.event.minecraft;

import me.quickscythe.api.event.Event;
import me.quickscythe.api.object.MinecraftServer;

public class ServerStatusChangeEvent extends Event {

    private String status;
    private MinecraftServer server;

    public ServerStatusChangeEvent(String status, MinecraftServer server) {
        this.status = status;
        this.server = server;
    }

    public String getStatus() {
        return status;
    }

    public MinecraftServer getServer() {
        return server;
    }

}
