package ua.wyverno.twitch.api.http.server.handlers.overlay.page;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class OverlayHandle implements HttpHandler {

    private static final Logger logger = LoggerFactory.getLogger(OverlayHandle.class);
    @Override
    public void handle(HttpExchange t) throws IOException {
        logger.debug("Start Overlay GET Handle");

        File index = new File("htmls/overlay/index.html");
        byte[] bytes = Files.readAllBytes(index.toPath());

        logger.debug("Read all bytes for response page!");

        String response = new String(bytes, StandardCharsets.UTF_8);
        logger.debug("Create response");

        t.sendResponseHeaders(200,response.length());
        t.getResponseHeaders().add("Content-Type","text/html; charset=UTF-8");
        logger.debug("Set code and content type");

        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
        logger.debug("Overlay GET Handler END");
    }
}
