package ua.wyverno.twitch.api.http.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;

public class CloseHandle implements HttpHandler {

    private static final Logger logger = LoggerFactory.getLogger(CloseHandle.class);

    @Override
    public void handle(HttpExchange t) throws IOException {
        logger.debug("Close POST!");

        String response = "CLOSE SERVER";

        t.sendResponseHeaders(200,response.getBytes().length);
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();

        t.getHttpContext().getServer().stop(0);
        logger.info("HTTP Server - is stop");
    }
}
