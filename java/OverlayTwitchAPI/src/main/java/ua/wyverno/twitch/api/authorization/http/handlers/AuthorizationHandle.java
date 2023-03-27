package ua.wyverno.twitch.api.authorization.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.twitch.api.http.server.HttpHandle;
import ua.wyverno.twitch.api.http.server.handlers.AbstractHandler;
import ua.wyverno.util.ExceptionToString;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@HttpHandle(path = "/authorization")
public class AuthorizationHandle extends AbstractHandler {

    private static final Logger logger = LoggerFactory.getLogger(AuthorizationHandle.class);

    private static final String authorizationURL;

    static {
        String tmp;
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("html/authorization/url.txt")) {
            assert is != null;
            tmp = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            logger.info("Authorization URL loaded successfully");
        } catch (IOException e) {
            logger.debug(ExceptionToString.getString(e));
            tmp = "";
        }

        authorizationURL = tmp;
    }

    @Override
    protected void handleHttp(HttpExchange t) throws IOException {
        logger.debug("Start Authorization GET Handler");

        if (authorizationURL.isEmpty()) {
            logger.error("AUTHORIZATION URL DONT HAS!!!");
            t.sendResponseHeaders(204,-1);
            t.close();
        } else {
            t.getResponseHeaders().add("Content-Type","text/html");
            t.sendResponseHeaders(200,authorizationURL.length());

            OutputStream os = t.getResponseBody();

            os.write(authorizationURL.getBytes());
            os.close();
        }

        logger.debug("End Authorization GET Handler");
    }
}
