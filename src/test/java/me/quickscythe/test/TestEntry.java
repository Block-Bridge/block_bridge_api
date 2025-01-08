package me.quickscythe.test;

import json2.JSONObject;
import me.quickscythe.Api;
import me.quickscythe.storage.StorageManager;
import me.quickscythe.v1.webapp.BinaryUtils;
import me.quickscythe.v2.BlockBridgeApiV2;

import java.util.stream.StreamSupport;

public class TestEntry {

    static Api botApp;
    static Api serverApp;

    public static void main(String[] args) {
//        botApp = new BlockBridgeApiV2();
////        serverApp = new BlockBridgeApi();
//        botApp.init(true);
//        StorageManager.getStorage().set("test", "test");
//        System.out.println("test: " + StorageManager.getStorage().get("test"));
//
//        StorageManager.getStorage().set("test.test", "test");
//        System.out.println("test.test: " + StorageManager.getStorage().get("test.test"));
//
//        System.out.print(StorageManager.getStorage().root().toString(2));
//
//        StorageManager.getStorage().save();

        BinaryUtils utils = new BinaryUtils();
        String start = new JSONObject().put("test", "test").put("test2",3).toString(2);
        System.out.println("Start: " + start);
        String convert = utils.toBinary(start);
        System.out.println("Convert: " + convert);
        String revert = utils.fromBinary(convert);
        System.out.println("Revert: " + revert);

    }

    public static Api getBotApp() {
        return botApp;
    }
}
