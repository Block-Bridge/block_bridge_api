package me.quickscythe;

import json2.JSONArray;
import json2.JSONException;
import json2.JSONObject;
import me.quickscythe.api.Config;
import me.quickscythe.utils.token.TokenManager;
import me.quickscythe.webapp.WebApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.util.Scanner;

public class BlockBridgeApi extends Config implements Api  {


    private final Logger logger = LoggerFactory.getLogger(BlockBridgeApi.class);
    private final TokenManager tokenManager = new TokenManager(this);
    private boolean DEBUG = false;

    private WebApp WEB_APP;
    private String token = null;

    public BlockBridgeApi() {
        super("webapp.json");
    }

    @Override
    public TokenManager getTokenManager() {
        return tokenManager;
    }



    public void init() {
        load();
        checkConfigDefaults();
        save();
        WEB_APP = new WebApp(this);
    }

    @Override
    public void validateToken() {
        logger.info("Validating Token: " + token);
        if (getData("check_token", false).has("error") || token == null) {
            logger.info("Token Invalid: " + (token == null ? "Token is null" : getData("check_token", false).getString("error")));
            token = generateNewToken();
            logger.info("New Token: " + token);
        }
    }

    private String generateNewToken() {
        try {
            logger.info("Generating new token");
            URL url = URI.create(URL() + APP_ENTRY() + "/token").toURL();
            String result = new Scanner(url.openStream(), "UTF-8").useDelimiter("\\A").next();
            logger.info(result);
            return new JSONObject(result).getString("success");
        } catch (IOException | JSONException e) {
            throw new RuntimeException("Failed to generate new token. Shutting down");
        }
    }

    @Override
    public JSONObject getData(String path) {
        return getData(path, true);
    }

    @Override
    public JSONObject getData(String path, boolean validate) {
        if (validate) validateToken();
        try {
            URL url = URI.create(URL() + APP_ENTRY() + "/" + APP_VERSION() + "/" + token + "/" + path).toURL();
            return new JSONObject(new Scanner(url.openStream(), "UTF-8").useDelimiter("\\A").next());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String URL() {
        return get().getString("api_url");
    }

    @Override
    public String APP_ENTRY() {
        return get().getString("app_entry_point");
    }

    @Override
    public String API_ENTRY() {
        return get().getString("api_entry_point");
    }

    @Override
    public String APP_VERSION() {
        return get().getString("app_version");
    }

    @Override
    public int WEB_PORT() {
        return get().getInt("web_port");
    }

    private void checkConfigDefaults() {

        setDefault("api_entry_point", "/api");
        setDefault("app_entry_point", "/app");
        setDefault("web_port", 8585);
        setDefault("token_valid_time", 24);
        setDefault("allow", new JSONArray());
    }

    @Override
    public void allow(String ip) {
        get().getJSONArray("allow").put(ip);
        save();
    }

    @Override
    public boolean isDebug() {
        return DEBUG;
    }

    @Override
    public WebApp getWebApp() {
        return WEB_APP;
    }



    @Override
    public int TOKEN_VALID_TIME() {
        return get().getInt("token_valid_time");
    }

    @Override
    public Logger getLogger() {
        return logger;
    }
}

