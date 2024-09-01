package me.quickscythe.api.event;

public class ServerPingEvent extends Event {
    private String from;
    private String status;

    public ServerPingEvent(String from, String status) {
        this.from = from;
        this.status = status;
    }

    public String getFromIp() {
        return from;
    }

    public String getStatus() {
        return status;
    }
}
