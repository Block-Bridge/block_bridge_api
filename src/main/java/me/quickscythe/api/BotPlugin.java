package me.quickscythe.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BotPlugin {

    private String name = null;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    public void enable(){
        System.out.println(name + " enabled");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Logger getLogger(){
        return logger;
    }
}
