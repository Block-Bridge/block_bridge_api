package me.quickscythe.api.config;

import me.quickscythe.blockbridge.core.BridgeIntegration;
import me.quickscythe.blockbridge.core.config.Config;
import me.quickscythe.blockbridge.core.config.ConfigTemplate;
import me.quickscythe.blockbridge.core.config.ConfigValue;
import me.quickscythe.blockbridge.core.utils.HashUtils;
import org.json.JSONArray;

import java.io.File;

@ConfigTemplate(name = "config")
public class DefaultConfig extends Config {

    @ConfigValue
    public String secretToken = HashUtils.hashString("defaultSecretToken");


    public DefaultConfig(File file, String name, BridgeIntegration integration) {
        super(file, name, integration);
    }
}
