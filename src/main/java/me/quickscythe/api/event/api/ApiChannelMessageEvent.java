package me.quickscythe.api.event.api;

import json2.JSONObject;
import spark.Request;

public class ApiChannelMessageEvent {

    JSONObject data;
    String message;
    String to;
    String action;
    Request req;


    public ApiChannelMessageEvent(Request req) {
        this.req = req;
        this.data = new JSONObject(req.body());
        if(!data.has("message"))
            data.put("message", "null");
        if(!data.has("to"))
            data.put("to", "null");
        if(!data.has("action"))
            data.put("action", "null");
        this.message = data.getString("message");
        this.to = data.getString("to");
        this.action = data.getString("action");
    }

    public JSONObject getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public String getTo() {
        return to;
    }

    public String getAction() {
        return action;
    }

    public Request getRequest() {
        return req;
    }
}
