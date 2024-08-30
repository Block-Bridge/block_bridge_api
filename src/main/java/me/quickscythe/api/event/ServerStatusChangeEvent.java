package me.quickscythe.api.event;

public class ServerStatusChangeEvent extends Event {
    private String server;
    private String status;

    public ServerStatusChangeEvent(String server, String status) {
        this.server = server;
        this.status = status;
    }

    public String getServer() {
        return server;
    }

    public String getStatus() {
        return status;
    }
}
