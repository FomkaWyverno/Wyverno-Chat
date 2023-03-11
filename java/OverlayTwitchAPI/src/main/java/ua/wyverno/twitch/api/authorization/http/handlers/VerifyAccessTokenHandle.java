package ua.wyverno.twitch.api.authorization.http.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.twitch.api.authorization.AboutAccount;
import ua.wyverno.twitch.api.authorization.Account;
import ua.wyverno.twitch.api.authorization.ConfigHandler;
import ua.wyverno.twitch.api.http.server.HttpHandle;

import java.io.IOException;
import java.io.OutputStream;

@HttpHandle(path = "/verifyAccessToken")
public class VerifyAccessTokenHandle implements HttpHandler {

    private static final Logger logger = LoggerFactory.getLogger(VerifyAccessTokenHandle.class);

    @Override
    public void handle(HttpExchange t) throws IOException {
        logger.debug("Start VerifyAccessTokenHandle");

        boolean isValid = ConfigHandler.getInstance().isValidAccessToken();

        logger.debug("Valid token? -> " + isValid);

        if (isValid) {

            Account account = Account.getInstance();

            AboutAccount aboutAccount = new AboutAccount(account.getDisplayName(),account.getProfileImageURL());

            String jsonAboutAccount = new ObjectMapper().writeValueAsString(aboutAccount);

            logger.debug("Send code 200");
            t.sendResponseHeaders(200,jsonAboutAccount.length());

            t.getResponseHeaders().add("Content-Type","application/json");

            OutputStream os = t.getResponseBody();

            os.write(jsonAboutAccount.getBytes());
            os.close();
        } else {
            logger.debug("Send code 401");
            t.sendResponseHeaders(401,0);
            t.getResponseHeaders().add("Content-Type","text/html");

            t.getResponseBody().close();
        }
        logger.debug("Close VerifyAccessTokenHandle");
    }
}
