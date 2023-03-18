package ua.wyverno.twitch.api.http.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.twitch.api.http.server.HttpHandle;
import ua.wyverno.twitch.api.http.server.handlers.main.page.MainHandle;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

@HttpHandle(path = "/")
public class ResourceHandle implements HttpHandler {

    private static final Logger logger = LoggerFactory.getLogger(ResourceHandle.class);

    @Override
    public void handle(HttpExchange t) throws IOException {
        byte[] response = null;
        String contentType = "text/plain";

        String path = t.getRequestURI().getPath();
        logger.debug("Resource Handler PATH: " + path);

        if (path.equals("/")) {
            new MainHandle().handle(t);
        } else {
            Path resource = Paths.get("./html/" + path);
            if (path.endsWith(".css")) {
                response = getResponse(resource);
                contentType = "text/css";
            } else if (path.endsWith(".js")) {
                response = getResponse(resource);
                contentType = "application/javascript";
            } else if (path.endsWith(".svg")){
                response = getResponse(resource);
                contentType = "image/svg+xml";
            } else if (path.endsWith(".ttf")) {
                response = getResponse(resource);
                contentType = "font/ttf";
            }

            if (response != null) {
                logger.trace("Length bytes response: " + response.length);
                t.getResponseHeaders().add("Content-Type", contentType);
                t.sendResponseHeaders(200, response.length);
                t.getResponseBody().write(response);
            } else {
                String errorMessage = "File not found!";
                t.sendResponseHeaders(404, errorMessage.length());
                t.getResponseBody().write(errorMessage.getBytes());
            }

            t.close();
        }
    }

    private byte[] getResponse(Path resource) throws IOException {
        return Files.readAllBytes(resource);
    }
}
