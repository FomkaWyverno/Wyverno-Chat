package ua.wyverno.twitch.api.http.server.handlers.main.page;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class MainHandle implements HttpHandler {

    private static final Logger logger = LoggerFactory.getLogger(MainHandle.class);
    @Override
    public void handle(HttpExchange t) throws IOException {
        logger.debug("Main page GET");
        File page = new File("html/main/index.html");
        byte[] pageBytes = Files.readAllBytes(page.toPath());
        logger.debug("Reading all bytes for Page.");

        String response = new String(pageBytes, StandardCharsets.UTF_8);

        t.sendResponseHeaders(200,response.length());
        t.getResponseHeaders().add("Content-Type","text/html; charset=UTF-8");

        OutputStream os = t.getResponseBody();

        os.write(response.getBytes());
        os.close();
        logger.debug("END Main page GET");
    }
}
