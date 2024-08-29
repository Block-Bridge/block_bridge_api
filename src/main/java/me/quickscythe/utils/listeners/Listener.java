package me.quickscythe.utils.listeners;

public interface Listener {

    interface JoinListener {
        void onJoin(String a, String b, String c);
    }
    interface LeaveListener {
        void onLeave(String a, String b, String c);
    }

    interface StatusListener {
        void onStatusChange(String a, String b, String c);
    }
}
