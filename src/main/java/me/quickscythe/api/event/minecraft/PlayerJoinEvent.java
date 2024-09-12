package me.quickscythe.api.event.minecraft;

import me.quickscythe.api.event.Event;
import me.quickscythe.api.object.Player;

public class PlayerJoinEvent extends Event {
    private Player player;

    public PlayerJoinEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
