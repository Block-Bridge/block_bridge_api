package me.quickscythe.web;
import me.quickscythe.Api;
import me.quickscythe.api.BotPlugin;
import me.quickscythe.api.listener.Listener;

import java.util.*;

import static spark.Spark.*;

public abstract class WebApp {

    private final Api bba;
    private HashMap<Listener, BotPlugin> listeners = new HashMap<>();

    public WebApp(Api bba) {
        this.bba = bba;

        setup();
    }

    protected void setup(){
        port(bba.WEB_PORT());
    }

    public Api getApi(){
        return bba;
    }

    public void addListener(BotPlugin plugin, Listener listener) {
        listeners.put(listener, plugin);
    }

    public Set<Listener> getListeners() {
        return listeners.keySet();
    }

    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    public void removeListeners(BotPlugin plugin) {
        List<Listener> remove = new ArrayList<>();
        for (Listener listener : listeners.keySet()) {

            if (listeners.get(listener).equals(plugin)) {
                remove.add(listener);
            }
        }
        for (Listener listener : remove) {
            removeListener(listener);
        }
    }
}



