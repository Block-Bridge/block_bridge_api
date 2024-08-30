package me.quickscythe.api.event;

public class PlayerLeaveEvent extends Event {
    private String player;
    private String server;
    private String uuid;

    public PlayerLeaveEvent(String player, String uuid, String server) {
        this.player = player;
        this.server = server;
        this.uuid = uuid;
    }

    public String getPlayer() {
        return player;
    }

    public String getServer() {
        return server;
    }

    public String getUuid() {
        return uuid;
    }
}
