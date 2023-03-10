package ua.wyverno.twitch.api.http.server.handlers.main.scripts;

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

@HttpHandle(path = "/main/scripts/script.js")
public class ScriptJSHandle implements HttpHandler {

    private static final Logger logger = LoggerFactory.getLogger(ScriptJSHandle.class);

    @Override
    public void handle(HttpExchange t) throws IOException {
        logger.debug("Start GET ScriptJSHandle");

        File script = new File("html/main/scripts/script.js");

        byte[] bytes = Files.readAllBytes(script.toPath());
        logger.debug("Reading all bytes script.js");

        String response = new String(bytes, StandardCharsets.UTF_8);

        t.sendResponseHeaders(200,response.length());
        t.getResponseHeaders().add("Content-Type","application/javascript");

        OutputStream os = t.getResponseBody();

        os.write(response.getBytes());
        os.close();
        logger.debug("End ScriptJSHandle");
    }
}
