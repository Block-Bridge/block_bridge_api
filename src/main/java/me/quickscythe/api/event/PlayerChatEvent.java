package me.quickscythe.api.event;

public class PlayerChatEvent extends Event {
    private String player;
    private String server;
    private String message;

    public PlayerChatEvent(String player, String server, String message) {
        this.player = player;
        this.server = server;
        this.message = message;
    }

    public String getPlayer() {
        return player;
    }

    public String getServer() {
        return server;
    }

    public String getMessage() {
        return message;
    }
}
