package me.quickscythe.webapp;

import ch.qos.logback.core.model.processor.DenyAllModelFilter;
import json2.JSONArray;
import json2.JSONObject;
import me.quickscythe.Api;
import me.quickscythe.api.BotPlugin;
import me.quickscythe.api.event.PlayerJoinEvent;
import me.quickscythe.api.event.PlayerLeaveEvent;
import me.quickscythe.api.event.ServerStatusChangeEvent;
import me.quickscythe.api.listener.Listener;
import me.quickscythe.api.object.MinecraftServer;
import me.quickscythe.api.object.Player;
import me.quickscythe.sql.SqlUtils;
import me.quickscythe.webapp.token.Token;
import spark.Route;

import java.util.*;

import static spark.Spark.get;
import static spark.Spark.port;

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

        bba.getLogger().info("WebApp started on port {}", bba.WEB_PORT());
    }

    private Route getApiData() {
        return (req, res) -> {
            String action = req.params(":action");
            if(action.equalsIgnoreCase("player_data")){
                String a = req.queryParams("a");
                if(a == null) return Feedback.Errors.json("No player provided");
                return bba.apiData("player_data?a=" + a);
            }
            return Feedback.Errors.json("No action taken.");
        };
    }

    public Player getPlayer(String uuid) {
        return new Player(bba.apiData("player_data?a=" + uuid));
    }

    private Route getNoPathError() {
        return (req, res) -> {
            res.type("application/json");
            return Feedback.Errors.json("No path provided");
        };
    }

    private Route getToken() {
        return (req, res) ->{
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
                ServerStatusChangeEvent e = new ServerStatusChangeEvent(a, new MinecraftServer(bba.apiData("server_data?a=" + b)));
                for (Listener listener : getListeners())
                    if (listener instanceof Listener.StatusListener)
                        ((Listener.StatusListener) listener).onStatusChange(e);
            }
            if (action.equalsIgnoreCase("join")) {
                PlayerJoinEvent e = new PlayerJoinEvent(getPlayer(a));
                for (Listener listener : getListeners())
                    if (listener instanceof Listener.JoinListener) ((Listener.JoinListener) listener).onJoin(e);
            }

            if (action.equalsIgnoreCase("leave")) {
                PlayerLeaveEvent e = new PlayerLeaveEvent(getPlayer(a));
                for (Listener listener : getListeners())
                    if (listener instanceof Listener.LeaveListener) ((Listener.LeaveListener) listener).onLeave(e);

            }

            if (action.equalsIgnoreCase("save_player")) {
                if(a==null||b==null||c==null)
                    return Feedback.Errors.json("Missing parameters");
                if(SqlUtils.getDatabase("core").update("UPDATE players SET username = ?, ip = ?, time = ? WHERE uuid = ?", a, c, new Date().getTime(), b) <=0){
                    SqlUtils.getDatabase("core").update("INSERT INTO players (uuid, username, ip, time) VALUES (?, ?, ?, ?)", b, a, c, new Date().getTime());
                }


            }
            if (action.equalsIgnoreCase("save_player")) {
                String d = req.queryParams("d");
                String e = req.queryParams("e");
                String f = req.queryParams("f");
                //name TEXT, ip TEXT, port INTEGER, motd MOTD, maxPlayers INTEGER, onlinePlayers INTEGER
                /**
                 * a=name
                 * b=ip
                 * c=port
                 * d=motd
                 * e=maxPlayers
                 * f=onlinePlayers
                 */
                if(a==null||b==null||c==null||d==null||e==null||f==null)

                    return Feedback.Errors.json("Missing parameters");

                if(SqlUtils.getDatabase("core").update("UPDATE servers SET ip = ?, port = ?, motd = ?, maxPlayers = ?, onlinePlayers = ? WHERE name = ?", b, c, d, e, f, a) <=0){
                    SqlUtils.getDatabase("core").update("INSERT INTO servers (name, ip, port, motd, maxPlayers, onlinePlayers) VALUES (?, ?, ?, ?, ?, ?)", a, b, c, d, e, f);
                }
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



