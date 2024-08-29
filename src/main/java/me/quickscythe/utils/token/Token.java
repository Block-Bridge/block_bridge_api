package me.quickscythe.utils.token;

import me.quickscythe.BlockBridgeApi;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Token {
    private String token;
    private String ip;
    private long created;

    public Token(String token, String ip) {
        this.token = token;
        this.ip = ip;
        this.created = new Date().getTime();
    }

    public String getToken() {
        return token;
    }

    public String getIp() {
        return ip;
    }

    public long getCreated(){
        return created;
    }

    public boolean isExpired() {
        return new Date().getTime() - created >= TimeUnit.MILLISECONDS.convert(BlockBridgeApi.TOKEN_VALID_TIME(), TimeUnit.HOURS);
    }
}
