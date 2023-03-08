package ua.wyverno;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.twitch.api.authorization.Authorization;
import ua.wyverno.util.ExceptionToString;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            logger.info("Start main class");
            new Authorization();
        } catch (Exception e) {
            logger.error(ExceptionToString.getString(e));
        }
    }
}
