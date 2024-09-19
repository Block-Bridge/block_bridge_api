package me.quickscythe.storage;

import json2.JSONObject;
import me.quickscythe.Api;

public class StorageManager {

    private static Storage storage;

    public static void init(Api api){
        storage = new Storage(api);
    }

    //StorageManager.getStorage().saveData("data", "value");

    public static Storage getStorage(){
        return storage;
    }
}
