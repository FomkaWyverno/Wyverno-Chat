package ua.wyverno.dropbox.auth.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpAccessTokenHandler implements HttpHandler {
    private static final Logger logger = LoggerFactory.getLogger(HttpAccessTokenHandler.class);

    private String accessToken;
    private final Object lock = new Object();

    @Override
    public void handle(HttpExchange exchange) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            JsonNode jsonNode = mapper.readTree(exchange.getRequestBody());
            logger.debug("Take JSON:\n{}", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode));

            String token = jsonNode.path("access_token").asText();

            String response;
            int code;
            if (!token.isEmpty()) {
                response = "OK";
                this.setAccessToken(token);
                code = 200;
            } else {
                response = "Bad Request";
                code = 400;
            }

            exchange.sendResponseHeaders(code, response.getBytes().length);
            exchange.getResponseBody().write(response.getBytes());
            exchange.close();
        } catch (Exception e) {
            logger.error("", e);
        }
    }


    public String getAccessToken() {
        synchronized (this.lock) {
            while (this.accessToken == null) {
                try {
                    logger.trace("Sleeping Thread in method getAccessToken()");
                    this.lock.wait();
                    logger.trace("Woke up Thread when was sleeping in getAccessToken()");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            logger.trace("Return Access Token");
            return this.accessToken;
        }
    }

    public void setAccessToken(String accessToken) {
        synchronized (this.lock) {
            logger.trace("Get Access Token from User!");
            this.accessToken = accessToken;
            this.lock.notifyAll();
            logger.trace("Notify All Threads in method setAccessToken(String accessToken)");
        }

    }
}
