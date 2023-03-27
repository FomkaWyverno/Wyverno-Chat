package ua.wyverno.twitch.api.http.server.handlers.overlay.page;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.twitch.api.http.server.HttpHandle;
import ua.wyverno.twitch.api.http.server.handlers.AbstractHandler;
import ua.wyverno.twitch.api.http.server.handlers.HtmlHandle;
import ua.wyverno.twitch.api.http.server.handlers.ResourceHandle;

import java.io.IOException;
import java.nio.file.Paths;

@HttpHandle(path = "/overlay")
public class OverlayHandle extends AbstractHandler {

    private static final Logger logger = LoggerFactory.getLogger(OverlayHandle.class);
    @Override
    protected void handleHttp(HttpExchange t) throws IOException {
        logger.debug("Start Overlay GET Handle");
        String path = t.getRequestURI().getPath();
        if (path.equals("/overlay")) {
            new HtmlHandle().handle(t, Paths.get("/html/overlay/index.html"));
        } else {
            new ResourceHandle().handle(t);
        }

    }
}
