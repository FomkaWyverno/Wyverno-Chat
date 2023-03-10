package ua.wyverno.twitch.api.authorization.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.Main;
import ua.wyverno.twitch.api.http.server.HttpHandle;

import java.io.IOException;

@HttpHandle(path = "/verifyAccessToken")
public class VerifyAccessTokenHandle implements HttpHandler {

    private static final Logger logger = LoggerFactory.getLogger(VerifyAccessTokenHandle.class);

    @Override
    public void handle(HttpExchange t) throws IOException {
        logger.debug("Start VerifyAccessTokenHandle");

        boolean valid = Main.getConfig().isValidAccessToken();

        logger.debug("Valid token? -> " + valid);

        if (valid) {
            logger.debug("Send code 200");
            t.sendResponseHeaders(200,-1);
        } else {
            logger.debug("Send code 401");
            t.sendResponseHeaders(401,-1);
        }
        t.close();
        logger.debug("Close VerifyAccessTokenHandle");
    }
}
