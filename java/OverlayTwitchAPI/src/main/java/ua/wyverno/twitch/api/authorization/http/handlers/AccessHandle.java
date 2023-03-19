package ua.wyverno.twitch.api.authorization.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.twitch.api.http.server.HttpHandle;
import ua.wyverno.twitch.api.http.server.handlers.HtmlHandle;

import java.io.IOException;
import java.nio.file.Paths;

@HttpHandle(path = "/access")
public class AccessHandle implements HttpHandler
{
    private static final Logger logger = LoggerFactory.getLogger(AccessHandle.class);

    @Override
    public void handle(HttpExchange t) throws IOException {
        logger.debug("Client AccessToken GET method");
        new HtmlHandle().handle(t, Paths.get("/html/authorization/index.html"));
    }
}
