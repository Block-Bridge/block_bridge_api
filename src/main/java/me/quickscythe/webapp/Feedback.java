package me.quickscythe.webapp;

public class Feedback {

    public static String json(String message) {
        return "{\"feedback\":\"" + message + "\"}";
    }

    public static class Errors {
        public static String json(String message) {
            return "{\"error\":\"" + message + "\"}";
        }
    }

    public static class Success {
        public static String json(String message) {
            return "{\"success\":\"" + message + "\"}";
        }
    }
}
