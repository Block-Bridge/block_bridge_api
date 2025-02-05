package me.quickscythe.api.v1.handlers;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.quickscythe.api.BridgeApi;
import me.quickscythe.api.web.Feedback;
import me.quickscythe.api.web.token.Token;
import me.quickscythe.blockbridge.core.server.BridgeServer;
import me.quickscythe.blockbridge.core.server.BridgeServlet;
import me.quickscythe.blockbridge.core.utils.NetworkUtils;
import org.json.JSONObject;

import java.io.IOException;

public class TokenHandler extends BridgeServlet {


    public TokenHandler(BridgeServer server) {
        super(server);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!(server().integration() instanceof BridgeApi api)) {
            System.out.println("Invalid API");

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("Missing token");
            return;
        }

        System.out.println("Handling token request");

        String requestString = NetworkUtils.streamToString(request.getInputStream()).trim();

        if (requestString.startsWith("{") && requestString.endsWith("}")) {
            JSONObject jsonRequest = new JSONObject(requestString);
            if (jsonRequest.has("test")){
                response.getWriter().write(jsonRequest.getString("test"));
                return;
            }
            if (jsonRequest.has("token")) {
                System.out.println("Token: " + jsonRequest.getString("token"));
                String tokenId = jsonRequest.getString("token");
                Token token = api.tokens().token(tokenId);

                if (api.tokens().valid(token, request.getRemoteAddr())) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write("Success");
                } else {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("Invalid token");
                }
            }
        } else {
            String jsonResponse = Feedback.Errors.json("Invalid request");
            System.out.println("Request: " + requestString);
            System.out.println("Response: " + jsonResponse);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(jsonResponse);

        }
    }


}

