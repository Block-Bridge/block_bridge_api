package me.quickscythe.test;


import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import me.quickscythe.api.BridgeApi;
import me.quickscythe.api.config.DefaultConfig;
import me.quickscythe.blockbridge.core.config.ConfigManager;
import me.quickscythe.blockbridge.core.server.BridgeHandler;
import me.quickscythe.blockbridge.core.utils.NetworkUtils;
import me.quickscythe.test.api.TestBridgeApi;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

public class ApiTests {

    @Test
    void launchApiCleanWhenDone() throws IOException {
        BridgeApi api = new TestBridgeApi();
        api.enable();
        DefaultConfig defaultConfig = ConfigManager.getConfig(api, DefaultConfig.class);
        assertTrue(api.tokens().request("127.0.0.1", defaultConfig.secretToken) != null, "Token request was denied");

        api.server().handle(new BridgeHandler(api.server()) {
            @Override
            public void handle(HttpContext context, URI uri, HttpExchange exchange) throws IOException {


                JSONObject request = new JSONObject(ApiTests.this.toString(exchange.getRequestBody()));

                // Set the response headers and status code
                exchange.sendResponseHeaders(200, 0);

                // Write the file bytes to the response body
                OutputStream os = exchange.getResponseBody();
                if (request.has("test"))
                    os.write(request.getString("test").getBytes());
                else
                    os.write("Test".getBytes());
                os.close();

            }
        }, "test");


        api.server().start();

        assertEquals("Test", NetworkUtils.request("http://127.0.0.1:9009/vTEST/test").trim(), "Server response was not as expected");

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
