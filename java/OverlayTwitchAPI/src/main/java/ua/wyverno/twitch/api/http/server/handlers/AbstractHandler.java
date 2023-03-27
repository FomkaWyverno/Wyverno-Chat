package ua.wyverno.twitch.api.http.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public abstract class AbstractHandler implements HttpHandler {

    private static final Logger logger = LoggerFactory.getLogger(AbstractHandler.class);

    @Override
    public void handle(HttpExchange t) throws IOException {
        logger.info("Path: " + t.getRequestURI().getPath());
        boolean isElectronApp = isElectronApp(t);
        logger.debug("Is electron client? - " + isElectronApp);
        if (isElectronApp) {
            this.handleHttp(t);
        } else {
            logger.debug("This app not electron app! Ignored handle");
            t.sendResponseHeaders(400,-1);
            t.close();
        }

    }

    protected abstract void handleHttp(HttpExchange t) throws IOException;


    private boolean isElectronApp(HttpExchange t) {
        List<String> userAgent = t.getRequestHeaders().get("User-agent");

        if (userAgent == null || userAgent.isEmpty()) return false;

        boolean isElectron = userAgent.stream().anyMatch(e -> e.contains("Electron"));
        logger.debug("User-agent: " + userAgent);
        return isElectron;
    }
}
