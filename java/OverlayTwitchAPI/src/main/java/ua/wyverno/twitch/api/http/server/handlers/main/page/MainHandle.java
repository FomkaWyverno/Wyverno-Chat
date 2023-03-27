package ua.wyverno.twitch.api.http.server.handlers.main.page;

import com.sun.net.httpserver.HttpExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.twitch.api.http.server.handlers.AbstractHandler;
import ua.wyverno.twitch.api.http.server.handlers.HtmlHandle;

import java.io.IOException;
import java.nio.file.Paths;

public class MainHandle extends AbstractHandler {

    private static final Logger logger = LoggerFactory.getLogger(MainHandle.class);
    @Override
    protected void handleHttp(HttpExchange t) throws IOException {
        logger.debug("MainHandler handle()");
        new HtmlHandle().handle(t, Paths.get("html/main/index.html"));
    }
}
