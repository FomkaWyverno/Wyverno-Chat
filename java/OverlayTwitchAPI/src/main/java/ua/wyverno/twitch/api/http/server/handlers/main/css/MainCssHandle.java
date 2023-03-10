package ua.wyverno.twitch.api.http.server.handlers.main.css;

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

@HttpHandle(path = "/main/css/style.css")
public class MainCssHandle implements HttpHandler {

    private static final Logger logger = LoggerFactory.getLogger(MainCssHandle.class);

    @Override
    public void handle(HttpExchange t) throws IOException {
        logger.debug("Main CSS GET");

        File css = new File("html/main/css/style.css");

        byte[] bytes = Files.readAllBytes(css.toPath());
        logger.debug("Reading all bytes for style.css");

        String response = new String(bytes, StandardCharsets.UTF_8);

        t.sendResponseHeaders(200,response.length());
        t.getResponseHeaders().add("Content-Type","text/css");

        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();

        logger.debug("End Main CSS GET Handler");
    }
}
