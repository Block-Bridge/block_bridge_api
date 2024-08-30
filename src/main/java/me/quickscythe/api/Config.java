package me.quickscythe.api;

import json2.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class Config {

    private JSONObject CONFIG;
    private String FILE;
    Logger logger = LoggerFactory.getLogger(Config.class);

    public Config(String file){
        FILE = file;
    }

    public void load(){
            StringBuilder stringBuilder = new StringBuilder();
            try {
                File config = new File(FILE);
                if (!config.exists()) if (config.createNewFile()) {
                    logger.info("Config file generated.");
                }
                BufferedReader reader = new BufferedReader(new FileReader(config));

                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                reader.close();


            } catch (IOException ex) {
                logger.error("Config File couldn't be generated or accessed. Please check console for more details.", ex);
            }
            String config = stringBuilder.toString();
            CONFIG = config.isEmpty() ? new JSONObject() : new JSONObject(config);

    }
    public void setDefault(String key, Object value) {
        setDefault(key, value, CONFIG);
    }

    public void setDefault(String key, Object value, JSONObject config) {
        if (!config.has(key)) config.put(key, value);
    }
    public JSONObject get() {
        return CONFIG;
    }
    public void save() {
        try {
            FileWriter f2 = new FileWriter("webapp.json", false);
            f2.write(CONFIG.toString(2));
            f2.close();
        } catch (IOException e) {
            logger.error("There was an error saving the config file.", e);
        }
    }
}
