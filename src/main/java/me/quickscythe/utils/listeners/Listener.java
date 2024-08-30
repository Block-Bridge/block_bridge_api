package me.quickscythe.utils.listeners;

import me.quickscythe.api.BotPlugin;

public interface Listener {


    interface JoinListener extends Listener {
        void onJoin(String a, String b, String c);
    }
    interface LeaveListener extends Listener {
        void onLeave(String a, String b, String c);
    }

    interface StatusListener extends Listener {
        void onStatusChange(String a, String b, String c);
    }
}
