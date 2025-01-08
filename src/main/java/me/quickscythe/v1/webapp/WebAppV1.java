package me.quickscythe.v1.webapp;

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
import me.quickscythe.web.Feedback;
import me.quickscythe.web.WebApp;
import me.quickscythe.web.token.Token;
import spark.Route;

import java.util.*;

import static spark.Spark.*;

public class WebAppV1 extends WebApp {

    public WebAppV1(Api api) {
        super(api);
    }

    @Override
    protected void setup(){
        super.setup();
        get(getApi().API_ENTRY(), getNoPathError());
        get(getApi().API_ENTRY() + "/:action", getApiData());
        get(getApi().APP_ENTRY(), getNoPathError());
        get(getApi().APP_ENTRY() + "/v1/:token/:action", getAction());
        get(getApi().APP_ENTRY() + "/token", getToken());
        get(getApi().APP_ENTRY() + "/tokens", getTokens());
        post(getApi().APP_ENTRY() + "/v1/:token/:action", getPostAction());
        getApi().getLogger().info("WebApp started on port {}", getApi().WEB_PORT());
    }

    private Route getPostAction() {
        return (req, res) -> {
            res.type("application/json");
            if (!getApi().getTokenManager().validToken(getApi().getTokenManager().getToken(req.params(":token")), req))
                return Feedback.Errors.json("Invalid token. IP match: " + (getApi().getTokenManager().getToken(req.params(":token")) != null ? getApi().getTokenManager().getToken(req.params(":token")).getIp().equals(req.ip()) : "false - No Token In DB"));
            Token token = getApi().getTokenManager().getToken(req.params(":token"));
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
            String token = getApi().getTokenManager().requestNewToken(req.ip());
            return token == null ? Feedback.Errors.json("Error generating token. IP Not allowed?") : Feedback.Success.json(token);
        };
    }

    private Route getTokens() {
        return (req, res) -> {
            res.type("application/json");
            JSONObject feedback = new JSONObject();
            feedback.put("tokens", new JSONArray());
            for (String token : getApi().getTokenManager().getTokens(req.ip())) {
                feedback.getJSONArray("tokens").put(token);
            }
            return feedback;
        };
    }

    private Route getAction() {
        return (req, res) -> {
            res.type("application/json");
            if (!getApi().getTokenManager().validToken(getApi().getTokenManager().getToken(req.params(":token")), req))
                return Feedback.Errors.json("Invalid token. IP match: " + (getApi().getTokenManager().getToken(req.params(":token")) != null ? getApi().getTokenManager().getToken(req.params(":token")).getIp().equals(req.ip()) : "false - No Token In DB"));
            Token token = getApi().getTokenManager().getToken(req.params(":token"));
            String action = req.params(":action");
            if (action.equalsIgnoreCase("check_token"))
                return getApi().getTokenManager().validToken(token, req) ? Feedback.Success.json("Valid token") : Feedback.Errors.json("Invalid token");
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
                getApi().getLogger().info("Ping to {} from {}", a, b);

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
                getApi().getLogger().info("Chat: {} - {} - {}", player.getName(), b, c);
            }
            if (action.equalsIgnoreCase("join") || action.equalsIgnoreCase("leave")) {
                /*
                 * a=uuid
                 */
                getApi().getLogger().info("Searching for player: {}", a);
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

    public void runTokenCheck() {
        List<String> remove_tokens = new ArrayList<>();
        for (Token token : getApi().getTokenManager().getTokens()) {
            if (token.isExpired()) {
                getApi().getLogger().info("Token {} has expired.", token.getToken());
                remove_tokens.add(token.getToken());
            }
        }
        for (String token : remove_tokens) {
            getApi().getTokenManager().removeToken(token);
        }
    }
}



