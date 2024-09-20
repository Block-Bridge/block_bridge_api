package me.quickscythe.storage;

import json2.JSONObject;
import me.quickscythe.Api;
import me.quickscythe.BlockBridgeApi;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Storage {

    JSONObject data = new JSONObject();
    File file = new File("BlockBridgeApi/storage.json");
    Api api;

    public Storage(Api api) {
        this.api = api;
        if(!file.getParentFile().exists()){
            api.getLogger().info("Creating new storage directory: {}", file.getParentFile().mkdirs());
//            TestEntry.getBotApp().getLogger().info("Creating new storage directory: {}", file.getParentFile().mkdirs());
        }
        if (!file.exists()) {
            try {
                api.getLogger().info("Creating new storage file: {}", file.createNewFile());
//                TestEntry.getBotApp().getLogger().info("Creating new storage file: {}", file.createNewFile());
            } catch (IOException e) {
                api.getLogger().error("There was an error creating {}", file.getName(), e);
//                TestEntry.getBotApp().getLogger().error("There was an error creating {}", file.getName(), e);
            }
        }
        try {
            String content = "";
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                content += scanner.nextLine();
            }
            scanner.close();
            data = new JSONObject(content);
        } catch (IOException e) {
            api.getLogger().error("There was an error reading {}", file.getName(), e);
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
        api.getLogger().info("Saving storage file...");
        try {
            FileWriter f2 = new FileWriter(file, false);
            f2.write(data.toString(2));
            f2.close();
        } catch (IOException e) {
            api.getLogger().error("There was an error saving {}", file.getName(), e);
        }

    }
}
