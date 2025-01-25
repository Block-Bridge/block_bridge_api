package me.quickscythe.test.api;

import me.quickscythe.api.BridgeApi;
import me.quickscythe.api.web.token.TokenManager;
import me.quickscythe.blockbridge.core.server.BridgeServer;

import java.io.File;

public class TestBridgeApi extends BridgeApi {
    private File dataFolder2;

    public TestBridgeApi(){
        super(new BridgeServer.ServerConfig(BridgeServer.ServerProtocol.HTTP, "127.0.0.1", 9009));
        dataFolder2 = new File("data");
        if(!dataFolder2.exists())
            logger().info("Creating api data folder: {}", dataFolder().mkdirs() ? "Success" : "Failure");

    }

    @Override
    public String version() {
        return "vTEST";
    }

    @Override
    public File dataFolder() {
        return dataFolder2;
    }
}
