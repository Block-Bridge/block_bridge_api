package me.quickscythe.storage;

import json2.JSONObject;

public class StorageManager {

    private static final Storage storage = new Storage();

    //StorageManager.getStorage().saveData("data", "value");

    public static Storage getStorage(){
        return storage;
    }
}
