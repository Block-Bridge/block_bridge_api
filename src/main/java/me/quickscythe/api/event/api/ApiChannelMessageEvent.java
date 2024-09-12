package me.quickscythe.api.event.api;

import json2.JSONObject;
import spark.Request;

public class ApiChannelMessageEvent {

    JSONObject data;
    String message;
    Request req;


    public ApiChannelMessageEvent(Request req) {
        this.req = req;
        this.data = new JSONObject(req.body());
        this.message = data.getString("message");
    }

    public JSONObject getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public Request getRequest() {
        return req;
    }
}
