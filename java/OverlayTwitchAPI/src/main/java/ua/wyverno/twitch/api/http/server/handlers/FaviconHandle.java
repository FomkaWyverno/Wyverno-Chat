package ua.wyverno.twitch.api.http.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ua.wyverno.twitch.api.http.server.HttpServer;

import java.io.IOException;

public class FaviconHandle implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        HttpServer.logger.debug("Client want get favicon.ico");
        exchange.sendResponseHeaders(204, -1);
        exchange.close();
        HttpServer.logger.debug("We send to client what we dont have it");
    }
}
