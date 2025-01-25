package me.quickscythe.api.config;

import me.quickscythe.blockbridge.core.BridgeIntegration;
import me.quickscythe.blockbridge.core.config.Config;
import me.quickscythe.blockbridge.core.config.ConfigTemplate;
import me.quickscythe.blockbridge.core.config.ConfigValue;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

@ConfigTemplate(name = "tokens")
public class TokenConfig extends Config {

    @ConfigValue
    public JSONArray tokens = new JSONArray();

    public TokenConfig(File file, String name, BridgeIntegration integration) {
        super(file, name, integration);
    }

    public boolean tokenExists(String token) {
        for(int i = 0; i < tokens.length(); i++) {
            JSONObject data = tokens.getJSONObject(i);
            if(data.getString("token").equals(token)) {
                return true;
            }
        }
        return false;
    }

    public JSONObject getToken(String token) {
        for(int i = 0; i < tokens.length(); i++) {
            JSONObject data = tokens.getJSONObject(i);
            if(data.getString("token").equals(token)) {
                return data;
            }
        }
        return null;
    }

    public void removeToken(String token) {
        for(int i = 0; i < tokens.length(); i++) {
            JSONObject data = tokens.getJSONObject(i);
            if(data.getString("token").equals(token)) {
                tokens.remove(i);
                save();
                return;
            }
        }
    }

    public JSONArray getTokens(String ip) {
        JSONArray tokens = new JSONArray();
        for(int i = 0; i < this.tokens.length(); i++) {
            JSONObject data = this.tokens.getJSONObject(i);
            if(data.getString("ip").equals(ip)) {
                tokens.put(data);
            }
        }
        return tokens;
    }
}
