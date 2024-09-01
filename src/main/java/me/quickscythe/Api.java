package me.quickscythe;

import json2.JSONObject;
import me.quickscythe.webapp.token.TokenManager;
import me.quickscythe.webapp.WebApp;
import org.slf4j.Logger;

public interface Api {

    void validateToken();

    JSONObject appData(String path);

    JSONObject appData(String path, boolean validate);

    JSONObject apiData(String path);

    void allow(String ip);
    boolean isDebug();

    WebApp getWebApp();
    Logger getLogger();
    TokenManager getTokenManager();

    void init(boolean webapp);


    int TOKEN_VALID_TIME();
    String URL();
    String APP_ENTRY();
    String API_ENTRY();
    String APP_VERSION();
    int WEB_PORT();


}
