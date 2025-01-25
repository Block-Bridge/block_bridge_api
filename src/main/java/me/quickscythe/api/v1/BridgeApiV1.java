package me.quickscythe.api.v1;

import me.quickscythe.api.BridgeApi;
import me.quickscythe.blockbridge.core.server.BridgeServer;

import java.io.File;

public class BridgeApiV1 extends BridgeApi {


    public BridgeApiV1(){
        super(new BridgeServer.ServerConfig(BridgeServer.ServerProtocol.HTTP, "127.0.0.1", 9009));
    }

    @Override
    public String version() {
        return "v1";
    }


}
