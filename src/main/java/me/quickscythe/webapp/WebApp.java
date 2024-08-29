package me.quickscythe.webapp;

import json2.JSONArray;
import json2.JSONObject;
import me.quickscythe.BlockBridgeApi;
import me.quickscythe.webapp.token.Token;
import me.quickscythe.webapp.token.TokenManager;

import java.util.ArrayList;
import java.util.List;

import static spark.Spark.get;
import static spark.Spark.port;

public class WebApp {



    public WebApp() {
        port(WEB_PORT());
        get(API_ENTRY_POINT(), (req, res) -> {
            res.type("application/json");
            return Feedback.Errors.json("No UUID provided");
        });


        get(APP_ENTRY_POINT(), (req, res) -> {
            res.type("application/json");
            return Feedback.Errors.json("No path provided");
        });

        get(APP_ENTRY_POINT() + "/v1/:token/:action", (req, res) -> {
            res.type("application/json");
            if (!TokenManager.validToken(TokenManager.getToken(req.params(":token")), req))
                return Feedback.Errors.json("Invalid token. IP match: " + (TokenManager.getToken(req.params(":token")) != null ? TokenManager.getToken(req.params(":token")).getIp().equals(req.ip()) : "false - No Token In DB"));
            Token token = TokenManager.getToken(req.params(":token"));
            String action = req.params(":action");
            if (action.equalsIgnoreCase("check_token"))
                return TokenManager.validToken(token, req) ? Feedback.Success.json("Valid token") : Feedback.Errors.json("Invalid token");
            String a = req.queryParams("a");
            String b = req.queryParams("b");
            String c = req.queryParams("c");
            if (action.equalsIgnoreCase("status")) {
                if (a == null)
                    return Feedback.Errors.json("No status provided");
            }
            if (action.equalsIgnoreCase("join")) {

            }
            if (action.equalsIgnoreCase("leave")) {

            }
            return Feedback.Success.json("Valid token. Action: " + req.params(":action"));


        });

        get(APP_ENTRY_POINT() + "/token", (req, res) ->

        {
            res.type("application/json");
            String token = TokenManager.requestNewToken(req.ip());
            return token == null ? Feedback.Errors.json("Error generating token. IP Not allowed?") : Feedback.Success.json(token);
        });

        get(APP_ENTRY_POINT() + "/tokens", (req, res) -> {
            res.type("application/json");
            JSONObject feedback = new JSONObject();
            feedback.put("tokens", new JSONArray());
            for (String token : TokenManager.getTokens(req.ip())) {
                feedback.getJSONArray("tokens").put(token);
            }
            return feedback;
        });

        BlockBridgeApi.getLogger().info("WebApp started on port " + WEB_PORT());
    }


    public void runTokenCheck() {
        List<String> remove_tokens = new ArrayList<>();
        for (Token token : TokenManager.getTokens()) {
            if (token.isExpired()) {
                BlockBridgeApi.getLogger().info("Token {} has expired.", token.getToken());
                remove_tokens.add(token.getToken());
            }
        }
        for (String token : remove_tokens) {
            TokenManager.removeToken(token);
        }
    }

    private String APP_ENTRY_POINT() {
        return BlockBridgeApi.getConfig().getString("app_entry_point");
    }

    private String API_ENTRY_POINT() {
        return BlockBridgeApi.getConfig().getString("api_entry_point");
    }

    private int WEB_PORT() {
        return BlockBridgeApi.getConfig().getInt("web_port");
    }
}

