package me.quickscythe.utils.token;

import json2.JSONArray;
import json2.JSONObject;
import me.quickscythe.Api;
import me.quickscythe.BlockBridgeApi;
import spark.Request;

import java.util.*;

public class TokenManager {

    private final Map<String, Token> TOKENS = new HashMap<>();
    private final BlockBridgeApi bba;

    public TokenManager(BlockBridgeApi bba) {
        this.bba = bba;
    }

    public String requestNewToken(String ip) {
        if (!bba.get().has("allow")) {
            bba.get().put("allow", new JSONArray());
        }
        bba.getLogger().info("Requesting token for {}", ip);
        boolean allowed = false;
        JSONObject data = bba.get();
        for (int i = 0; i != data.getJSONArray("allow").length(); i++) {
            if (data.getJSONArray("allow").getString(i).equals(ip)) {
                allowed = true;
                break;
            }
        }
        if (allowed) {
            String token = UUID.randomUUID().toString();
            while (TOKENS.containsKey(token)) token = UUID.randomUUID().toString();
            TOKENS.put(token, new Token(token, ip, bba));
            return token;
        }
        return null;
    }

    public void removeToken(String token) {
        TOKENS.remove(token);
    }

    public Collection<Token> getTokens() {
        return TOKENS.values();
    }

    public String[] getTokens(String ip) {
        List<String> tokens = new ArrayList<>();
        for (Map.Entry<String, Token> entry : TOKENS.entrySet()) {
            if (entry.getValue().getIp().equals(ip)) {
                tokens.add(entry.getKey());
            }
        }
        return tokens.toArray(String[]::new);
    }

    public boolean validToken(Token token, Request request) {
        return token != null && token.getIp().equals(request.ip()) && !token.isExpired();
    }


    public Token getToken(String token) {
        return TOKENS.getOrDefault(token, null);
    }
}
