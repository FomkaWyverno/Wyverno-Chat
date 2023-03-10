package ua.wyverno.twitch.api.http.server.handlers.overlay.css;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.twitch.api.http.server.HttpHandle;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@HttpHandle(path = "/overlay/style/style.css")
public class OverlayCSSHandle implements HttpHandler {

    private static final Logger logger = LoggerFactory.getLogger(OverlayCSSHandle.class);

    @Override
    public void handle(HttpExchange t) throws IOException {
        logger.debug("Start OverlayCSS GET Handle");

        File index = new File("html/overlay/css/style.css");
        byte[] bytes = Files.readAllBytes(index.toPath());

        logger.debug("Read all bytes for response page!");

        String response = new String(bytes, StandardCharsets.UTF_8);
        logger.debug("Create response");

        t.sendResponseHeaders(200,response.length());
        t.getResponseHeaders().add("Content-Type","text/css; charset=UTF-8");
        logger.debug("Set code and content type");

        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
        logger.debug("OverlayCSS GET Handler END");
    }
}
