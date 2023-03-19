package ua.wyverno.twitch.api.http.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.twitch.api.http.server.HttpHandle;
import ua.wyverno.twitch.api.http.server.handlers.main.page.MainHandle;
import ua.wyverno.util.ExceptionToString;
import ua.wyverno.util.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@HttpHandle(path = "/")
public class ResourceHandle implements HttpHandler {

    private static final Logger logger = LoggerFactory.getLogger(ResourceHandle.class);

    @Override
    public void handle(HttpExchange t) throws IOException {
        String path = t.getRequestURI().getPath();
        logger.debug("Resource Handler PATH: " + path);

        if (path.equals("/")) {
            new MainHandle().handle(t);
        } else {
            Path resource = Paths.get("html/" + path);

            final String contentType = this.getContentType(path);
            ResourceLoader.getResourceAsBytes(resource.toString())
                    .ifPresentOrElse(response -> {
                        try {
                            logger.trace("Length bytes response: " + response.length);
                            t.getResponseHeaders().add("Content-Type", contentType);
                            t.sendResponseHeaders(200, response.length);
                            t.getResponseBody().write(response);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }, () -> {
                        try {
                            String errorMessage = "File not found!";
                            t.sendResponseHeaders(404, errorMessage.length());
                            t.getResponseBody().write(errorMessage.getBytes());
                        } catch (IOException e) {
                            logger.error(ExceptionToString.getString(e));
                        }
                    });
            t.close();
        }
    }

    private String getContentType(String path) {
        String contentType = "text/plain";
        if (path.endsWith(".css")) {
            contentType = "text/css";
        } else if (path.endsWith(".js")) {
            contentType = "application/javascript";
        } else if (path.endsWith(".svg")) {
            contentType = "image/svg+xml";
        } else if (path.endsWith(".ttf")) {
            contentType = "font/ttf";
        }

        return contentType;
    }
}
