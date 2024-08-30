package me.quickscythe;

import me.quickscythe.api.BotPlugin;

public class BlockBridgePlugin extends BotPlugin {

    public BlockBridgePlugin() {
        setName("BlockBridgeCore");
    }

    public void enable() {
        System.out.println(getName() + " enabled");
    }
}
