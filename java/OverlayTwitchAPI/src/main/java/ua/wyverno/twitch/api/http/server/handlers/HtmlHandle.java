package ua.wyverno.twitch.api.http.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class HtmlHandle {

    private static final Logger logger = LoggerFactory.getLogger(HtmlHandle.class);


    public void handle(HttpExchange t, Path resource) throws IOException {
        logger.debug("HTML Handle Resource PATH: "+resource);

        String response = Files.readString(resource);

        if (response != null) {
            logger.debug("Handle HTML Response is found!");
            t.getResponseHeaders().add("Content-Type","text/html");
            t.sendResponseHeaders(200,response.getBytes().length);
            t.getResponseBody().write(response.getBytes());
        } else {
            logger.debug("Page not found!");
            String errorMessage = "Page not found!";
            t.sendResponseHeaders(404, errorMessage.length());
            t.getResponseBody().write(errorMessage.getBytes());
        }

        t.close();
    }
}
