package me.quickscythe.api;

import me.quickscythe.api.config.DefaultConfig;
import me.quickscythe.api.config.TokenConfig;
import me.quickscythe.api.web.token.TokenManager;
import me.quickscythe.blockbridge.core.BridgeIntegration;
import me.quickscythe.blockbridge.core.config.ConfigManager;
import me.quickscythe.blockbridge.core.server.BridgeServer;

import java.io.File;
import java.util.Optional;

public abstract class BridgeApi extends BridgeIntegration {


    private TokenManager tokenManager;
    private final File dataFolder;

    public BridgeApi(BridgeServer.ServerConfig serverConfig) {
        super(Optional.of(serverConfig));
        this.dataFolder = new File("data");
        if(!dataFolder.exists())
            logger().info("Creating api data folder: {}", dataFolder.mkdirs() ? "Success" : "Failure");
    }


    @Override
    public void enable() {
        ConfigManager.registerConfig(this, DefaultConfig.class);
        ConfigManager.registerConfig(this, TokenConfig.class);

        tokenManager = new TokenManager(this);

    }

    public TokenManager tokens(){
        return tokenManager;
    }

    @Override
    public String name() {
        return "Api(" + version() + ")";
    }

    public abstract String version();

    @Override
    public File dataFolder() {
        return dataFolder;
    }
}
