package me.quickscythe.test;

import me.quickscythe.BlockBridgeApi;
import me.quickscythe.storage.StorageManager;
import me.quickscythe.webapp.WebApp;

public class TestEntry {

    static BlockBridgeApi botApp;
    static BlockBridgeApi serverApp;

    public static void main(String[] args) {
        botApp = new BlockBridgeApi();
//        serverApp = new BlockBridgeApi();
        botApp.init(true);
        StorageManager.getStorage().set("test", "test");
        System.out.println("test: " + StorageManager.getStorage().get("test"));

        StorageManager.getStorage().set("test.test", "test");
        System.out.println("test.test: " + StorageManager.getStorage().get("test.test"));

        System.out.print(StorageManager.getStorage().root().toString(2));

        StorageManager.getStorage().save();
    }

    public static BlockBridgeApi getBotApp() {
        return botApp;
    }
}
