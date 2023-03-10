package ua.wyverno.twitch.api.authorization.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ua.wyverno.twitch.api.http.server.HttpHandle;

import java.io.IOException;

@HttpHandle(path = "/verifyAccessToken")
public class VerifyAccessTokenHandle implements HttpHandler {
    @Override
    public void handle(HttpExchange t) throws IOException {

    }
}
