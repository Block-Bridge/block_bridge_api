package me.quickscythe.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BotPlugin {

    private String name = null;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    public void enable(){
        logger.info("{} enabled", name);
    }

    public void disable(){
        logger.info("{} disabled", name);
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
