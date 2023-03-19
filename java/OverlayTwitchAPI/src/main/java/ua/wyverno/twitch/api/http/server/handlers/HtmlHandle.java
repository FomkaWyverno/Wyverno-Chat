package ua.wyverno.twitch.api.http.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.util.ExceptionToString;
import ua.wyverno.util.ResourceLoader;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class HtmlHandle {

    private static final Logger logger = LoggerFactory.getLogger(HtmlHandle.class);


    public void handle(HttpExchange t, Path resource) throws IOException {
        logger.debug("HTML Handle Resource PATH: "+resource);

        ResourceLoader.getResourceAsBytes(resource.toString())
                .ifPresentOrElse(bytes -> {
                    try {
                        logger.debug("Handle HTML Response is found!");
                        t.getResponseHeaders().add("Content-Type","text/html");
                        t.sendResponseHeaders(200,bytes.length);
                        t.getResponseBody().write(bytes);
                    } catch (IOException e) {
                        logger.error(ExceptionToString.getString(e));
                    }
                }, () -> {
                    try {
                        logger.debug("Page not found!");
                        String errorMessage = "Page not found!";
                        t.sendResponseHeaders(404, errorMessage.length());
                        t.getResponseBody().write(errorMessage.getBytes());
                    } catch (IOException e) {
                        logger.error(ExceptionToString.getString(e));
                    }
                });
        t.close();
    }
}
