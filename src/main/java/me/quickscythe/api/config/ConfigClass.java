package me.quickscythe.api.config;


import json2.JSONObject;
import me.quickscythe.api.BotPlugin;

/**
 * Easier way to specify a class as a ConfigFile manager.
 */
public class ConfigClass {
    protected final ConfigFile CONFIG;

    /**
     * @param plugin     Plugin to register ConfigFile.
     * @param configFile String to save file. Do not include file extension.
     */
    public ConfigClass(BotPlugin plugin, String configFile) {
        CONFIG = ConfigFileManager.getFile(plugin, configFile);
    }

    /**
     * @param plugin     Plugin to register ConfigFile.
     * @param configFile String to save file. Do not include file extension.
     * @param resource   String path to plugin resource. Include file extension.
     */
    public ConfigClass(BotPlugin plugin, String configFile, String resource) {
        CONFIG = ConfigFileManager.getFile(plugin, configFile, resource);
    }

    /**
     * @param plugin     Plugin to register ConfigFile.
     * @param configFile String to save file. Do not include file extension.
     * @param defaults   JSONObject of default values to populate ConfigFile.
     */
    public ConfigClass(BotPlugin plugin, String configFile, JSONObject defaults) {
        CONFIG = ConfigFileManager.getFile(plugin, configFile, defaults);
    }

    public ConfigFile getConfig(){
        return CONFIG;
    }

    /**
     * Save ConfigFile
     */
    public void finish() {
        CONFIG.save();
    }

    public void setDefault(String key, Object value) {
        this.setDefault(key, value, this.CONFIG.getData());
    }

    public void setDefault(String key, Object value, JSONObject config) {
        if (!config.has(key)) {
            config.put(key, value);
        }

    }
}
