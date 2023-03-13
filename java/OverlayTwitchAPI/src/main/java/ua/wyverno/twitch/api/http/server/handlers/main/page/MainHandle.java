package ua.wyverno.twitch.api.http.server.handlers.main.page;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.twitch.api.http.server.handlers.HtmlHandle;

import java.io.IOException;
import java.nio.file.Paths;

public class MainHandle implements HttpHandler {

    private static final Logger logger = LoggerFactory.getLogger(MainHandle.class);
    @Override
    public void handle(HttpExchange t) throws IOException {
        logger.debug("MainHandler handle()");
        new HtmlHandle().handle(t, Paths.get("./html/main/index.html"));
    }
}
