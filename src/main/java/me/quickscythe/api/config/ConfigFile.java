package me.quickscythe.api.config;


import json2.JSONObject;
import me.quickscythe.api.BotPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * ConfigFile is a class for creating custom config files.
 * @author QuickSctythe
 */
public class ConfigFile implements Config {

    final JSONObject defaults;
    JSONObject data;
    File file;
    BotPlugin plugin;

    /**
     * Generate new ConfigFile instance. Should only be accessed by {@link ConfigFileManager}
     * @param plugin Plugin that registers the ConfigFile.
     * @param file Physical location the ConfigFile will be saved to.
     * @param defaults Default values to populate ConfigFile. Can be empty.
     */
     ConfigFile(BotPlugin plugin, File file, JSONObject defaults) {
        this.plugin = plugin;
        StringBuilder data = new StringBuilder();
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                data.append(scanner.nextLine());
            }
            scanner.close();
        } catch (IOException e) {
            plugin.getLogger().error("Error", e);
        }
        this.data = data.toString().isEmpty() ? defaults : new JSONObject(data.toString());
        this.defaults = new JSONObject(defaults.toString());
        this.file = file;
    }

    public void save() {
         plugin.getLogger().info("Saving " + plugin.getName() + "/" + file.getName());
        try {
            FileWriter f2 = new FileWriter(file, false);
            f2.write(data.toString(2));
            f2.close();
        } catch (IOException e) {
            plugin.getLogger().error("There was an error saving {}", file.getName(), e);
        }
    }

    /**
     * Gets the plugin that registered the config file. This is also the asset file the config will be saved under.
     * @return JavaPlugin
     */
    public BotPlugin getPlugin(){
        return plugin;
    }

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }

    public void reset() {
        this.data = new JSONObject(defaults.toString());
        save();
    }
}
