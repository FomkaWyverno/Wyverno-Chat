package ua.wyverno.dropbox.auth.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.util.http.ParserParameters;

import java.io.InputStream;
import java.net.URL;

public class HttpAuthHandler implements HttpHandler {

    private static final Logger logger = LoggerFactory.getLogger(HttpAuthHandler.class);

    @Override
    public void handle(HttpExchange exchange) {
        try {

            String code = ParserParameters
                    .getParameters(exchange.getRequestURI())
                    .get("code");

            URL url = HttpAuthHandler.class.getResource("/html/index.html");
            InputStream is = HttpAuthHandler.class.getResourceAsStream("/html/index.html");

            logger.trace("Resource URL is null? " + (url == null));
            logger.trace("InputStream Resource is null? " + (is == null));

            if (is != null) {
                logger.debug("Html loading");
                byte[] bytes = is.readAllBytes();
                exchange.getResponseHeaders().add("Content-Type", "text/html");
                exchange.sendResponseHeaders(200, bytes.length);
                exchange.getResponseBody().write(bytes);
            } else {
                String error = "An error occurred on the server";
                exchange.sendResponseHeaders(501, error.getBytes().length);
                exchange.getResponseBody().write(error.getBytes());
            }


            exchange.close();

        } catch (Exception e) {
            logger.error("", e);
        }
    }
}
