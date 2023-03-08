package ua.wyverno;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.twitch.api.authorization.AccessTokenNoLongerValidException;
import ua.wyverno.twitch.api.authorization.Authorization;
import ua.wyverno.twitch.api.http.server.HttpServer;
import ua.wyverno.util.ExceptionToString;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            logger.info("Start main class");
            HttpServer httpServer = new HttpServer();
            httpServer.start();
            try {
                new Authorization(httpServer);
            } catch (AccessTokenNoLongerValidException e) {
                logger.error(ExceptionToString.getString(e));
            }

        } catch (Exception e) {
            logger.error(ExceptionToString.getString(e));
        }
    }
}
