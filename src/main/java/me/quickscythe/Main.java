package me.quickscythe;

import me.quickscythe.api.BridgeApi;
import me.quickscythe.api.v1.BridgeApiV1;
import me.quickscythe.api.web.token.Token;
import me.quickscythe.blockbridge.core.utils.TaskScheduler;

import java.util.HashMap;
import java.util.Map;

public class Main {

    private static Map<Integer, Runnable> tasks = new HashMap<>();
    private static Map<Integer, Runnable> removeTasks = new HashMap<>();
    private static Map<Integer, Runnable> addTasks = new HashMap<>();

    private static BridgeApi api;

    public static void main(String[] args) {
        start(new BridgeApiV1());
    }

    public static void start(BridgeApi api) {
        api.enable();
        scheduleTasks();
        registerTask(() -> {
            for (int i = 0; i != api.tokens().tokens().length(); i++) {
                if (new Token(api.tokens().tokens().getJSONObject(i), api.tokens()).isExpired()) {
                    api.tokens().tokens().remove(i);
                }
            }
        });


        //Set handlers

        try {
            api.server().start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static int registerTask(Runnable task) {
        int id = tasks.size();
        while (tasks.containsKey(id)) id = id + 1;
        addTasks.put(id, task);
        return id;
    }

    private static void scheduleTasks() {
        TaskScheduler.scheduleAsyncTask(() -> {
            tasks.putAll(addTasks);
            addTasks.clear();
            long now = System.currentTimeMillis();
            tasks.forEach((id, task) -> {

                task.run();
            });

            for (Map.Entry<Integer, Runnable> entry : removeTasks.entrySet()) {
                tasks.remove(entry.getKey());
            }
            removeTasks.clear();

            scheduleTasks();
        }, 10, java.util.concurrent.TimeUnit.SECONDS);
    }
}
