package me.quickscythe;

import json2.JSONArray;
import json2.JSONObject;
import me.quickscythe.webapp.WebApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class BlockBridgeApi {


    private static boolean DEBUG = false;
    private static JSONObject CONFIG;
    private static WebApp WEB_APP;
    private static Logger logger = LoggerFactory.getLogger(BlockBridgeApi.class);


    public static void init() {


        CONFIG = loadConfig();
        checkConfigDefaults();
        saveConfig();
        WEB_APP = new WebApp();

    }

    private static void checkConfigDefaults() {

        setDefault("command_prefix", "!");
        setDefault("cmd_channel", 0L);
        setDefault("log_channel", 0L);
        setDefault("api_entry_point", "/api");
        setDefault("app_entry_point", "/app");
        setDefault("web_port", 8585);
        setDefault("token_valid_time", 24);
        setDefault("allow", new JSONArray());
    }

    private static void setDefault(String key, Object value) {
        setDefault(key, value, CONFIG);
    }

    private static void setDefault(String key, Object value, JSONObject config) {
        if (!config.has(key)) config.put(key, value);
    }

    private static JSONObject loadConfig() {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            File config = new File("config.json");
            if (!config.exists()) if (config.createNewFile()) {
                logger.info("Config file generated.");
            }
            BufferedReader reader = new BufferedReader(new FileReader("config.json"));

            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            reader.close();


        } catch (IOException ex) {
            logger.error("Config File couldn't be generated or accessed. Please check console for more details.", ex);
        }
        String config = stringBuilder.toString();
        return config.isEmpty() ? new JSONObject() : new JSONObject(config);
    }


    public static void saveConfig() {
        try {
            FileWriter f2 = new FileWriter("config.json", false);
            f2.write(CONFIG.toString(2));
            f2.close();
        } catch (IOException e) {
            logger.error("There was an error saving the config file.", e);
        }
    }

    public static boolean isDebug() {
        return DEBUG;
    }


    public static WebApp getWebApp() {
        return WEB_APP;
    }

    public static JSONObject getConfig() {
        return CONFIG;
    }

    public static int TOKEN_VALID_TIME() {
        return CONFIG.getInt("token_valid_time");
    }



    public static Logger getLogger() {
        return logger;
    }
}