package me.quickscythe.api.listener;

import me.quickscythe.api.event.PlayerJoinEvent;
import me.quickscythe.api.event.PlayerLeaveEvent;
import me.quickscythe.api.event.ServerStatusChangeEvent;

public interface Listener {


    interface JoinListener extends Listener {
        void onJoin(PlayerJoinEvent event);
    }
    interface LeaveListener extends Listener {
        void onLeave(PlayerLeaveEvent event);
    }

    interface StatusListener extends Listener {
        void onStatusChange(ServerStatusChangeEvent event);
    }
}
