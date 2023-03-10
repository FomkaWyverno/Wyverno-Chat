package ua.wyverno.twitch.api.http.server.handlers.main.scripts;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ua.wyverno.twitch.api.http.server.HttpHandle;

import java.io.IOException;

@HttpHandle(path = "/main/scripts/script.js")
public class ScriptJSHandle implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        
    }
}
