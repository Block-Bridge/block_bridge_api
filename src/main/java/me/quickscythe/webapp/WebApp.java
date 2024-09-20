package me.quickscythe.webapp;

import json2.JSONArray;
import json2.JSONObject;
import me.quickscythe.Api;
import me.quickscythe.api.BotPlugin;
import me.quickscythe.api.event.api.ApiChannelMessageEvent;
import me.quickscythe.api.event.minecraft.PlayerJoinEvent;
import me.quickscythe.api.event.minecraft.PlayerLeaveEvent;
import me.quickscythe.api.event.minecraft.ServerStatusChangeEvent;
import me.quickscythe.api.listener.Listener;
import me.quickscythe.api.object.MinecraftServer;
import me.quickscythe.api.object.Player;
import me.quickscythe.storage.Storage;
import me.quickscythe.storage.StorageManager;
import me.quickscythe.webapp.token.Token;
import spark.Route;

import java.util.*;

import static spark.Spark.*;

public class WebApp {

    private final Api bba;
    private HashMap<Listener, BotPlugin> listeners = new HashMap<>();

    public WebApp(Api bba) {
        this.bba = bba;
        port(bba.WEB_PORT());
        get(bba.API_ENTRY(), getNoPathError());

        get(bba.API_ENTRY() + "/:action", getApiData());

        get(bba.APP_ENTRY(), getNoPathError());

        get(bba.APP_ENTRY() + "/v1/:token/:action", getAction());

        get(bba.APP_ENTRY() + "/token", getToken());

        get(bba.APP_ENTRY() + "/tokens", getTokens());

        post(bba.APP_ENTRY() + "/v1/:token/:action", getPostAction());

        bba.getLogger().info("WebApp started on port {}", bba.WEB_PORT());
    }

    private Route getPostAction() {
        return (req, res) -> {
            res.type("application/json");
            if (!bba.getTokenManager().validToken(bba.getTokenManager().getToken(req.params(":token")), req))
                return Feedback.Errors.json("Invalid token. IP match: " + (bba.getTokenManager().getToken(req.params(":token")) != null ? bba.getTokenManager().getToken(req.params(":token")).getIp().equals(req.ip()) : "false - No Token In DB"));
            Token token = bba.getTokenManager().getToken(req.params(":token"));
            String action = req.params(":action");
            if (action.equalsIgnoreCase("send_message")) {

                ApiChannelMessageEvent e = new ApiChannelMessageEvent(req);
                for (Listener listener : getListeners())
                    if (listener instanceof Listener.ApiChannelListener apilistener) apilistener.onMessage(e);
                return Feedback.Success.json("Message sent.");
            }


            return Feedback.Errors.json("No action taken.");
        };
    }

    private Route getApiData() {
        return (req, res) -> {
            res.type("application/json");
            String action = req.params(":action");
            String a = req.queryParams("a");
            if (a == null) return Feedback.Errors.json("No perimeter provided");
            if (a.equalsIgnoreCase("this")) a = a.equalsIgnoreCase("this") ? req.ip() : a;
            if (action.equalsIgnoreCase("server_data")) {
                Storage storage = StorageManager.getStorage();
                String key = a.replaceAll("\\.", "_");
                if (storage.get("servers." + key) == null) return Feedback.Errors.json("Server not found");
                return Feedback.Objects.json(new MinecraftServer((JSONObject) storage.get("servers." + key)));
            }

            if (action.equalsIgnoreCase("player_data")) {
                Storage storage = StorageManager.getStorage();
                if (storage.get("players." + a) == null) return Feedback.Errors.json("Player not found");
                return Feedback.Objects.json(new Player((JSONObject) storage.get("players." + a)));
            }

            return Feedback.Errors.json("No action taken.");
//            return Feedback.Errors.json("No action taken.");
        };
    }

//    private JSONObject generateServerFromResultSet(ResultSet rs) throws SQLException {
//        return new JSONObject().put("name", rs.getString("name")).put("ip", rs.getString("ip")).put("port", rs.getInt("port")).put("motd", rs.getString("motd")).put("maxPlayers", rs.getInt("maxPlayers")).put("onlinePlayers", rs.getString("onlinePlayers"));
//    }


    private Route getNoPathError() {
        return (req, res) -> {
            res.type("application/json");
            return Feedback.Errors.json("No path provided");
        };
    }

    private Route getToken() {
        return (req, res) -> {
            res.type("application/json");
            String token = bba.getTokenManager().requestNewToken(req.ip());
            return token == null ? Feedback.Errors.json("Error generating token. IP Not allowed?") : Feedback.Success.json(token);
        };
    }

    private Route getTokens() {
        return (req, res) -> {
            res.type("application/json");
            JSONObject feedback = new JSONObject();
            feedback.put("tokens", new JSONArray());
            for (String token : bba.getTokenManager().getTokens(req.ip())) {
                feedback.getJSONArray("tokens").put(token);
            }
            return feedback;
        };
    }

    private Route getAction() {
        return (req, res) -> {
            res.type("application/json");
            if (!bba.getTokenManager().validToken(bba.getTokenManager().getToken(req.params(":token")), req))
                return Feedback.Errors.json("Invalid token. IP match: " + (bba.getTokenManager().getToken(req.params(":token")) != null ? bba.getTokenManager().getToken(req.params(":token")).getIp().equals(req.ip()) : "false - No Token In DB"));
            Token token = bba.getTokenManager().getToken(req.params(":token"));
            String action = req.params(":action");
            if (action.equalsIgnoreCase("check_token"))
                return bba.getTokenManager().validToken(token, req) ? Feedback.Success.json("Valid token") : Feedback.Errors.json("Invalid token");
            String a = req.queryParams("a");
            String b = req.queryParams("b");
            String c = req.queryParams("c");
            if (action.equalsIgnoreCase("status")) {
                if (a == null) return Feedback.Errors.json("No status provided");
                ServerStatusChangeEvent e = new ServerStatusChangeEvent(a, new MinecraftServer((JSONObject) StorageManager.getStorage().get("servers." + req.ip().replaceAll("\\.", "_"))));
                for (Listener listener : getListeners())
                    if (listener instanceof Listener.StatusListener)
                        ((Listener.StatusListener) listener).onStatusChange(e);
            }
            if (action.equalsIgnoreCase("ping")) {
                if (a == null || b == null) return Feedback.Errors.json("No server provided");
                /*
                 * a=server
                 * b=ip
                 */
                bba.getLogger().info("Ping to {} from {}", a, b);

            }
            if (action.equalsIgnoreCase("chat")) {
                if (a == null || b == null || c == null) return Feedback.Errors.json("Missing parameters");
                /*
                 * a=uuid
                 * b=message
                 * c=time_sent
                 */


//                StorageManager.getStorage().get("test.message.1");
//                StorageManager.getStorage().set("test.message.1", "Hello, World!"); // {"test":{"message":{"1":"Hello, World!"}}}
//                StorageManager.getStorage().set("test.message.2", "Hello, World!"); // {"test":{"message":{"1":"Hello, World!","2":"Hello, World!"}}}

                Player player = new Player((JSONObject) StorageManager.getStorage().get("players." + a));
                bba.getLogger().info("Chat: {} - {} - {}", player.getName(), b, c);
            }
            if (action.equalsIgnoreCase("join") || action.equalsIgnoreCase("leave")) {
                /*
                 * a=uuid
                 */
                bba.getLogger().info("Searching for player: {}", a);
                Player player = new Player((JSONObject) StorageManager.getStorage().get("players." + a));
                if (action.equalsIgnoreCase("join")) {
                    PlayerJoinEvent e = new PlayerJoinEvent(player);
                    for (Listener listener : getListeners())
                        if (listener instanceof Listener.JoinListener sub) sub.onJoin(e);
                }
                if (action.equalsIgnoreCase("leave")) {
                    PlayerLeaveEvent e = new PlayerLeaveEvent(player);
                    for (Listener listener : getListeners())
                        if (listener instanceof Listener.LeaveListener sub) sub.onLeave(e);
                }


            }

            if (action.equalsIgnoreCase("save_player")) {
                if (a == null || b == null || c == null) return Feedback.Errors.json("Missing parameters");
                /*
                 * a=username
                 * b=uuid
                 * c=ip
                 */

                Storage storage = StorageManager.getStorage();
                storage.set("players." + b + ".username", a);
                storage.set("players." + b + ".ip", c);
                storage.set("players." + b + ".time", new Date().getTime());
                storage.set("players." + b + ".uuid", b);
                storage.save();


            }
            if (action.equalsIgnoreCase("save_server")) {
                String d = req.queryParams("d");
                String e = req.queryParams("e");
                /*
                 * a=name
                 * b=port
                 * c=motd
                 * d=maxPlayers
                 * e=onlinePlayers
                 */
                if (a == null || b == null || c == null || d == null || e == null)
                    return Feedback.Errors.json("Missing parameters");
                String ip = req.ip();
                String key = ip.replaceAll("\\.", "_");
                Storage storage = StorageManager.getStorage();
                storage.set("servers." + key + ".name", a);
                storage.set("servers." + key + ".port", b);
                storage.set("servers." + key + ".motd", c);
                storage.set("servers." + key + ".maxPlayers", d);
                storage.set("servers." + key + ".onlinePlayers", e);
                storage.set("servers." + key + ".ip", ip);
                storage.save();


            }
            return Feedback.Success.json("Valid token. Action: " + req.params(":action"));
        };
    }

    public void addListener(BotPlugin plugin, Listener listener) {
        listeners.put(listener, plugin);
    }

    public Set<Listener> getListeners() {
        return listeners.keySet();
    }

    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    public void removeListeners(BotPlugin plugin) {
        List<Listener> remove = new ArrayList<>();
        for (Listener listener : listeners.keySet()) {

            if (listeners.get(listener).equals(plugin)) {
                remove.add(listener);
            }
        }
        for (Listener listener : remove) {
            removeListener(listener);
        }
    }

    public void runTokenCheck() {
        List<String> remove_tokens = new ArrayList<>();
        for (Token token : bba.getTokenManager().getTokens()) {
            if (token.isExpired()) {
                bba.getLogger().info("Token {} has expired.", token.getToken());
                remove_tokens.add(token.getToken());
            }
        }
        for (String token : remove_tokens) {
            bba.getTokenManager().removeToken(token);
        }
    }
}



