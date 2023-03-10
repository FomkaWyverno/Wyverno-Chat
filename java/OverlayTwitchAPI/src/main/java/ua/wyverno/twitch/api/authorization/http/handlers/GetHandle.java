package ua.wyverno.twitch.api.authorization.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.twitch.api.http.server.HttpServer;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class GetHandle implements HttpHandler
{
    private static final Logger logger = LoggerFactory.getLogger(GetHandle.class);

    @Override
    public void handle(HttpExchange t) throws IOException {
        logger.debug("Client GET method");
        File index = new File("html/authorization/index.html");
        byte[] indexBytes = Files.readAllBytes(index.toPath());
        logger.debug("Read all bytes from index.html ");

        String response = new String(indexBytes, StandardCharsets.UTF_8);

        t.sendResponseHeaders(200, response.length());
        t.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");

        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
        logger.debug("End GET Method.");
    }
}
