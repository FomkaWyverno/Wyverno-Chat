package ua.wyverno.twitch.api.authorization.http.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.twitch.api.authorization.*;
import ua.wyverno.twitch.api.http.server.HttpHandle;

import java.io.IOException;
import java.io.OutputStream;

@HttpHandle(path = "/logging")
public class LoggingHandle implements HttpHandler {

    private static final Logger logger = LoggerFactory.getLogger(LoggingHandle.class);

    @Override
    public void handle(HttpExchange t) throws IOException {
        logger.debug("Start LoggingHandle");

        boolean isValid = ConfigHandler.getInstance().isValidAccessToken();

        logger.debug("Valid token? -> " + isValid);
        try {
            if (isValid) {

                Account account = Authorization.registerAccount(ConfigHandler.getInstance().getAccessToken());

                AboutAccount aboutAccount = new AboutAccount(account.getDisplayName(),account.getProfileImageURL());

                String jsonAboutAccount = new ObjectMapper().writeValueAsString(aboutAccount);

                logger.debug("Send code 200");
                t.sendResponseHeaders(200,jsonAboutAccount.length());

                t.getResponseHeaders().add("Content-Type","application/json");

                OutputStream os = t.getResponseBody();

                os.write(jsonAboutAccount.getBytes());
                os.close();
            } else {
                throw new AccessTokenNoLongerValidException();
            }
        } catch (AccessTokenNoLongerValidException e) {
            logger.debug("Send code 401");
            t.sendResponseHeaders(401,0);
            t.getResponseHeaders().add("Content-Type","text/html");

            t.getResponseBody().close();
        }

        logger.debug("Close VerifyAccessTokenHandle");
    }
}
