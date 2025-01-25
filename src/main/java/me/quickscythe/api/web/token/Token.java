package me.quickscythe.api.web.token;

import me.quickscythe.api.BridgeApi;
import org.json.JSONObject;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Token {
    private final String token;
    private final String ip;
    private final long created;
    private final TokenManager manager;

    public Token(JSONObject data, TokenManager manager){
        this.token = data.getString("token");
        this.ip = data.getString("ip");
        this.created = data.getLong("created");
        this.manager = manager;
    }

    public Token(String token, String ip, TokenManager manager) {
        this.token = token;
        this.ip = ip;
        this.created = new Date().getTime();
        this.manager = manager;
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
        return new Date().getTime() - created >= TimeUnit.MILLISECONDS.convert(manager.tokenValidTime(), TimeUnit.HOURS);
    }

    public JSONObject json(){
        JSONObject json = new JSONObject();
        json.put("token", token);
        json.put("ip", ip);
        json.put("created", created);
        return json;
    }
}
