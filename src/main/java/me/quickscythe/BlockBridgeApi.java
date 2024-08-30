package me.quickscythe;

import json2.JSONArray;
import json2.JSONException;
import json2.JSONObject;
import me.quickscythe.api.config.ConfigClass;
import me.quickscythe.webapp.token.TokenManager;
import me.quickscythe.webapp.WebApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Scanner;

public class BlockBridgeApi extends ConfigClass implements Api {


    private final Logger logger = LoggerFactory.getLogger(BlockBridgeApi.class);
    private final TokenManager tokenManager = new TokenManager(this);
    private boolean DEBUG = false;

    private WebApp WEB_APP = null;
    private String token = null;

    public BlockBridgeApi() {
        super(new BlockBridgePlugin(), "webapp");
    }

    @Override
    public TokenManager getTokenManager() {
        return tokenManager;
    }


    public void init(boolean webapp) {
        checkConfigDefaults();
        getConfig().save();
        if (webapp)
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
        return getConfig().getData().getString("api_url");
    }

    @Override
    public String APP_ENTRY() {
        return getConfig().getData().getString("app_entry_point");
    }

    @Override
    public String API_ENTRY() {
        return getConfig().getData().getString("api_entry_point");
    }

    @Override
    public String APP_VERSION() {
        return getConfig().getData().getString("app_version");
    }

    @Override
    public int WEB_PORT() {
        return getConfig().getData().getInt("web_port");
    }

    private void checkConfigDefaults() {

        setDefault("api_entry_point", "/api");
        setDefault("app_entry_point", "/app");
        setDefault("api_url", "http://localhost:8585");
        setDefault("app_version", "v1");
        setDefault("web_port", 8585);
        setDefault("token_valid_time", 24);
        setDefault("allow", new JSONArray());
    }

    @Override
    public void allow(String ip) {
        getConfig().getData().getJSONArray("allow").put(ip);
        getConfig().save();
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
        return getConfig().getData().getInt("token_valid_time");
    }

    @Override
    public Logger getLogger() {
        return logger;
    }
}

