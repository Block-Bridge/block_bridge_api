package me.quickscythe.storage;

import json2.JSONObject;
import me.quickscythe.TestEntry;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Storage {

    JSONObject data = new JSONObject();
    File file = new File("BlockBridgeApi/storage.json");

    public Storage() {
        if(!file.getParentFile().exists()){
            TestEntry.getBotApp().getLogger().info("Creating new storage directory: {}", file.getParentFile().mkdirs());
        }
        if (!file.exists()) {
            try {
                TestEntry.getBotApp().getLogger().info("Creating new storage file: {}", file.createNewFile());
            } catch (IOException e) {
                TestEntry.getBotApp().getLogger().error("There was an error creating {}", file.getName(), e);
            }
        }

    }

    public void set(String path, Object value) {
        //Save data to storage
        String[] paths = path.split("\\.");
        ;
        JSONObject current = data;
        for (int i = 0; i < paths.length; i++) {
//            System.out.println("Path: " + paths[i]);
            if (i == paths.length - 1) {
//                System.out.println("Setting: " + paths[i] + " to " + value);
                current.put(paths[i], value);
            } else {
//                System.out.println("Checking: " + paths[i]);
                if (!current.has(paths[i]) || !(current.get(paths[i]) instanceof JSONObject)) {
//                    System.out.println("Creating: " + paths[i]);
                    current.put(paths[i], new JSONObject());
                }
//                System.out.println("Moving to: " + paths[i]);
                current = current.getJSONObject(paths[i]);
            }
        }
    }


    public Object get(String path) {
        //Save data to storage
        String[] paths = path.split("\\.");
        ;
        JSONObject current = data;
        for (int i = 0; i < paths.length; i++) {
            if (i == paths.length - 1) {
                return current.get(paths[i]);
            } else {
                if (!current.has(paths[i])) {
                    return null;
                }
                current = current.getJSONObject(paths[i]);
            }
        }
        return current;

    }

    public JSONObject root() {
        return data;
    }

    public void save() {
        TestEntry.getBotApp().getLogger().info("Saving {}", file.getName());
        try {
            FileWriter f2 = new FileWriter(file, false);
            f2.write(data.toString(2));
            f2.close();
        } catch (IOException e) {
            TestEntry.getBotApp().getLogger().error("There was an error saving {}", file.getName(), e);
        }

    }
}
