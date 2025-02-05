package me.quickscythe.api.v1;

import me.quickscythe.api.BridgeApi;
import me.quickscythe.api.v1.handlers.HealthReportHandler;
import me.quickscythe.api.v1.handlers.TokenHandler;
import me.quickscythe.blockbridge.core.server.BridgeHandler;
import me.quickscythe.blockbridge.core.server.BridgeServer;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletHolder;

import java.io.File;

public class BridgeApiV1 extends BridgeApi {


    public BridgeApiV1(){
        super(new BridgeServer.ServerConfig(BridgeServer.ServerProtocol.HTTP, "127.0.0.1", 9009));
    }

    @Override
    public void enable() {
        super.enable();
        server().handler().handle("tokens", new TokenHandler(server()), "token");
        server().handler().handle("healthreport", new HealthReportHandler(server()), "healthreport/*");

    }

    @Override
    public String version() {
        return "v1";
    }


}
