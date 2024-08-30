package me.quickscythe;

import json2.JSONObject;
import me.quickscythe.utils.token.TokenManager;
import me.quickscythe.webapp.WebApp;
import org.slf4j.Logger;

public interface Api {

    void validateToken();

    JSONObject getData(String path);

    JSONObject getData(String path, boolean validate);

    void allow(String ip);
    void saveConfig();
    boolean isDebug();
    WebApp getWebApp();
    JSONObject getConfig();
    Logger getLogger();
    TokenManager getTokenManager();


    int TOKEN_VALID_TIME();
    String URL();
    String APP_ENTRY();
    String API_ENTRY();
    String APP_VERSION();
    int WEB_PORT();


}
