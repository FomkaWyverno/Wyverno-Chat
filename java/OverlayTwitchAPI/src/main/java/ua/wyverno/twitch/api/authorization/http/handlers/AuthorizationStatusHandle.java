package ua.wyverno.twitch.api.authorization.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.twitch.api.authorization.ConfigHandler;
import ua.wyverno.twitch.api.http.server.HttpHandle;

import java.io.IOException;

@HttpHandle(path = "/authorization-status")
public class AuthorizationStatusHandle implements HttpHandler {

    private static final Logger logger = LoggerFactory.getLogger(AuthorizationHandle.class);

    @Override
    public void handle(HttpExchange t) throws IOException {
        ConfigHandler config = ConfigHandler.getInstance();
        boolean isHasAccessToken = config.getAccessToken() != null && !config.getAccessToken().isEmpty() && config.isValidAccessToken();

        String value = String.valueOf(isHasAccessToken);

        logger.trace("AuthorizationStatus = " + value);
        t.sendResponseHeaders(200,value.length());
        t.getResponseBody().write(value.getBytes());
        t.getResponseBody().close();
    }
}
