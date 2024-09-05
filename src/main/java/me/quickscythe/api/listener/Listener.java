package me.quickscythe.api.listener;

import me.quickscythe.api.event.*;

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
    interface ChatListener extends Listener {
        void onPlayerChat(PlayerChatEvent event);
    }
    interface PingListener extends Listener {
        void onPing(ServerPingEvent event);
    }
}
