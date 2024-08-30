package me.quickscythe.api.config;


import json2.JSONObject;
import me.quickscythe.api.BotPlugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ConfigFileManager {

    private static final Map<String, ConfigFile> FILE_MAP = new HashMap<>();



    /**
     * Gets existing ConfigFile. Does NOT generate new one.
     * @param filename String key file is stored under, does not include file extension.
     * @return ConfigFile or null if not existing
     */
    public static ConfigFile getFile(String filename){
        return FILE_MAP.getOrDefault(filename, null);
    }

    /**
     * Get or generate ConfigFile.
     * @param plugin Plugin to generate file under, and to check for resources.
     * @param filename String key file will be stored as. Do not include file extension.
     * {@return New or generated ConfigFile}
     */
    public static ConfigFile getFile(BotPlugin plugin, String filename) {
        return getFile(plugin, filename, new JSONObject());
    }


    /**
     * Get or generate ConfigFile.
     * @param plugin Plugin to generate file under, and to check for resources.
     * @param filename String key file will be stored as. Do not include file extension.
     * @param defaults JSONObject to load into file if empty.
     * {@return New or generated ConfigFile}
     */
    public static ConfigFile getFile(BotPlugin plugin, String filename, JSONObject defaults) {
        String key = plugin.getName() + "/" + filename;
        if (!FILE_MAP.containsKey(key)) {
            File file = new File(plugin.getName() + "/" + filename + ".json");
            if (!file.exists()) {
                try {
                    if(!file.getParentFile().exists())
                        if(!file.getParentFile().mkdir())
                            throw new IOException("Couldn't create plugin config folder.");
                    if (!file.createNewFile())
                        throw new IOException("Couldn't create file (" + filename + ".json)");
                } catch (IOException e) {
                    plugin.getLogger().error("error", e);
                }
            }
            ConfigFile config = new ConfigFile(plugin, file, defaults);
            FILE_MAP.put(key, config);
        }
        return FILE_MAP.get(key);
    }


    /**
     * Get or generate ConfigFile.
     * @param plugin Plugin to generate file under, and to check for resources.
     * @param filename String key file will be stored as. Do not include file extension.
     * @param resource String path to plugin resource. File extension must be included.
     * {@return New or generated ConfigFile}
     */
    public static ConfigFile getFile(BotPlugin plugin, String filename, String resource) {

        JSONObject defaults = new JSONObject();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(plugin.getClass().getResourceAsStream(resource)))) {
            StringBuilder data = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                data.append(line);
                data.append("\n");
            }
            defaults = data.toString().isEmpty() ? defaults : new JSONObject(data.toString());
        } catch (IOException e) {
            plugin.getLogger().error("Error", e);
        }
        return getFile(plugin, filename, defaults);
    }

    /**
     * Get all file names.
     *
     * @return {@code Set<String>} of filenames.
     */
    public static Set<String> getFiles() {
        return FILE_MAP.keySet();
    }
}
