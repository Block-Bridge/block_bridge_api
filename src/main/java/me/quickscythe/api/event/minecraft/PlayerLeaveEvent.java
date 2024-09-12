package me.quickscythe.api.event.minecraft;

import me.quickscythe.api.event.Event;
import me.quickscythe.api.object.Player;

public class PlayerLeaveEvent extends Event {
    private Player player;

    public PlayerLeaveEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
