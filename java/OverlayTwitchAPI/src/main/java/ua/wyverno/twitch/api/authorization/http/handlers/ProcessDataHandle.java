package ua.wyverno.twitch.api.authorization.http.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.twitch.api.authorization.ConfigHandler;
import ua.wyverno.twitch.api.http.server.HttpHandle;

import java.io.*;

@HttpHandle(path = "/processData")
public class ProcessDataHandle implements HttpHandler {

    private static final Logger logger = LoggerFactory.getLogger(ProcessDataHandle.class);
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        logger.debug("POST /processData");
        InputStream inputStream = exchange.getRequestBody();
        logger.debug("Get accessTokenJSON");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String accessTokenJSON = reader.readLine();
        reader.close();

        logger.debug("RequestBody -> " + accessTokenJSON);

        ConfigHandler.getInstance().putAccessToken(new ObjectMapper().readTree(accessTokenJSON).get("accessToken").asText());

        String response = "OK";
        exchange.sendResponseHeaders(200, response.getBytes().length);
        logger.debug("Send response Headers 200");
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
        logger.debug("END POST /processData");
    }
}
