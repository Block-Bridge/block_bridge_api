package me.quickscythe.api.web.token;

import me.quickscythe.api.BridgeApi;
import me.quickscythe.api.config.DefaultConfig;
import me.quickscythe.api.config.TokenConfig;
import me.quickscythe.blockbridge.core.config.ConfigManager;
import org.json.JSONArray;
import spark.Request;

import java.util.UUID;

public class TokenManager {

    private final BridgeApi api;

    private final TokenConfig config;


    public TokenManager(BridgeApi api) {
        this.api = api;
        this.config = ConfigManager.getConfig(api, TokenConfig.class);
    }

    public String request(String ip, String secret) {
//        if (!bba.config().getData().has("allow")) {
//            bba.config().getData().put("allow", new JSONArray());
//        }
        api.logger().info("Requesting token for {}", ip);
        DefaultConfig defaultConfig = ConfigManager.getConfig(api, DefaultConfig.class);
        boolean allowed = secret.equals(defaultConfig.secretToken);
//        JSONObject data = bba.config().getData();
//        for (int i = 0; i != data.getJSONArray("allow").length(); i++) {
//            if (data.getJSONArray("allow").getString(i).equals(ip)) {
//                allowed = true;
//                break;
//            }
//        }
        if (allowed) {
            String token = UUID.randomUUID().toString();
            while (config.tokenExists(token)) token = UUID.randomUUID().toString();
            config.tokens.put(new Token(token, ip, this).json());
            config.save();
            return token;
        }
        api.logger().info("Request for token from {} denied.", ip);
        return null;
    }

    public void remove(String token) {
        config.removeToken(token);
    }

    public JSONArray tokens() {
        return config.tokens;
    }

    public JSONArray tokens(String ip) {
        return config.getTokens(ip);
    }

    public boolean valid(Token token, String ip) {
        return token != null && token.getIp().equals(ip) && !token.isExpired();
    }


    public Token token(String token) {
        return new Token(config.getToken(token), this);
    }

    public long tokenValidTime() {
        return 1L;
    }
}
