package me.quickscythe.webapp;

import json2.JSONArray;
import json2.JSONObject;
import me.quickscythe.Api;
import me.quickscythe.BlockBridgeApi;
import me.quickscythe.api.BotPlugin;
import me.quickscythe.utils.listeners.Listener;
import me.quickscythe.utils.token.Token;
import me.quickscythe.utils.token.TokenManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static spark.Spark.get;
import static spark.Spark.port;

public class WebApp {

    private HashMap<Listener, BotPlugin> listeners = new HashMap<>();
    private final Api bba;
    
    public WebApp(Api bba) {
        this.bba = bba;
        port(bba.WEB_PORT());
        get(bba.API_ENTRY(), (req, res) -> {
            res.type("application/json");
            return Feedback.Errors.json("No UUID provided");
        });


        get(bba.APP_ENTRY(), (req, res) -> {
            res.type("application/json");
            return Feedback.Errors.json("No path provided");
        });

        get(bba.APP_ENTRY() + "/v1/:token/:action", (req, res) -> {
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
                if (a == null)
                    return Feedback.Errors.json("No status provided");
                for(Listener listener : getListeners())
                    if(listener instanceof Listener.StatusListener)
                        ((Listener.StatusListener) listener).onStatusChange(a, b, c);
            }
            if (action.equalsIgnoreCase("join")) {
                for(Listener listener : getListeners())
                    if(listener instanceof Listener.JoinListener)
                        ((Listener.JoinListener) listener).onJoin(a, b, c);


            }
            if (action.equalsIgnoreCase("leave")) {
                for(Listener listener : getListeners())
                    if(listener instanceof Listener.LeaveListener)
                        ((Listener.LeaveListener) listener).onLeave(a, b, c);

            }
            return Feedback.Success.json("Valid token. Action: " + req.params(":action"));


        });

        get(bba.APP_ENTRY() + "/token", (req, res) ->

        {
            res.type("application/json");
            String token = bba.getTokenManager().requestNewToken(req.ip());
            return token == null ? Feedback.Errors.json("Error generating token. IP Not allowed?") : Feedback.Success.json(token);
        });

        get(bba.APP_ENTRY() + "/tokens", (req, res) -> {
            res.type("application/json");
            JSONObject feedback = new JSONObject();
            feedback.put("tokens", new JSONArray());
            for (String token : bba.getTokenManager().getTokens(req.ip())) {
                feedback.getJSONArray("tokens").put(token);
            }
            return feedback;
        });

        bba.getLogger().info("WebApp started on port {}", bba.WEB_PORT());
    }

    public void addListener(BotPlugin plugin, Listener listener){
        listeners.put(listener,plugin);
    }

    public Set<Listener> getListeners() {
        return listeners.keySet();
    }

    public void removeListener(Listener listener){
        listeners.remove(listener);
    }

    public void removeListeners(BotPlugin plugin){
        List<Listener> remove = new ArrayList<>();
        for(Listener listener : listeners.keySet()){
            if(listeners.get(listener).equals(plugin)){
                remove.add(listener);
            }
        }
        for(Listener listener : remove){
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



