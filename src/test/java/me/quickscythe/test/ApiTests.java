package me.quickscythe.test;


import me.quickscythe.api.BridgeApi;
import me.quickscythe.api.config.DefaultConfig;
import me.quickscythe.api.v1.handlers.TokenHandler;
import me.quickscythe.blockbridge.core.config.ConfigManager;
import me.quickscythe.blockbridge.core.utils.NetworkUtils;
import me.quickscythe.test.api.TestBridgeApi;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.junit.jupiter.api.Assertions.*;

public class ApiTests {

    @Test
    void launchApiCleanWhenDone() throws Exception {
        BridgeApi api = new TestBridgeApi();
        api.enable();
        DefaultConfig defaultConfig = ConfigManager.getConfig(api, DefaultConfig.class);
        assertNotNull(api.tokens().request("127.0.0.1", defaultConfig.secretToken), "Token request was denied");

        api.server().handler().handle("token", new TokenHandler(api.server()), "token/*");

        api.server().start();

        JSONObject request = new JSONObject();
        request.put("test", "Test");
        assertEquals("Test", NetworkUtils.post("http://127.0.0.1:9009/vTEST/token", request).trim(), "Server response was not as expected");

        api.destroy();

        assertFalse(api.dataFolder().exists());


    }

    String toString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String read;

        while ((read = br.readLine()) != null) {
            sb.append(read);
        }

        br.close();
        return sb.toString();
    }

}
