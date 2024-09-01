package me.quickscythe.api.event;

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
