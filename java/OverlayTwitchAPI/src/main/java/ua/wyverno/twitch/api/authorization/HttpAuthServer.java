package ua.wyverno.twitch.api.authorization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.util.ExceptionToString;

import java.awt.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class HttpAuthServer {

    private static final Logger logger = LoggerFactory.getLogger(HttpAuthServer.class);

    //DEFAULT VARIABLES
    private final    Object lockObject = new Object();
    private static final int DEFAULT_PORT = 2828;
    private final HttpServer httpServer;
    private boolean isRunServer = false;
    private static final String authURL =
    "https://id.twitch.tv/oauth2/authorize?client_id=znxb14or3tj0cm6e1pixh7zijlsgua&redirect_uri=http%3A%2F%2Flocalhost%3A2828/access&response_type=token&scope=channel%3Aread%3Aredemptions+chat%3Aread";

    private ResultAsk resultAsk = null;

    public HttpAuthServer() throws IOException {
        this(DEFAULT_PORT);
    }
    public HttpAuthServer(int port) throws IOException {
        this.httpServer = HttpServer.create(new InetSocketAddress(port),0);

        this.httpServer.createContext("/access",new GetHandle());
        this.httpServer.createContext("/processData",new PostHandle(this));
        this.httpServer.createContext("/favicon.ico",new FaviconHandle());
    }

    public void start() {
        this.isRunServer = true;
        logger.info("HTTP Server is starting");
        this.httpServer.start();
    }

    public void askAuthorization() {
        try {
            Desktop.getDesktop().browse(new URL(authURL).toURI());
        } catch (IOException | URISyntaxException e) {
            logger.error(ExceptionToString.getString(e));
        }
    }

    public ResultAsk getResultAsk() throws Exception {
        if (!isRunServer) {
            throw new Exception("HTTP SERVER NOT START! YOU NEED START SERVER AFTER GET RESULT");
        }
        synchronized (lockObject) {
            while (resultAsk == null) {
                logger.debug("resultAsk = null, so Thread WAIT");
                lockObject.wait();
            }

            logger.debug("return resultAsk");
            return this.resultAsk;
        }

    }

    private void setResultAsk(ResultAsk resultAsk) {
        this.resultAsk = resultAsk;
        logger.info("Result Ask to set -> " + resultAsk.toString());
        synchronized (lockObject) {
            lockObject.notifyAll();
        }
    }

    public static class ResultAsk {
        private String accessToken;
        private String scope;
        private String tokenType;

        public String getAccessToken() {
            return accessToken;
        }

        public String getScope() {
            return scope;
        }

        public String getTokenType() {
            return tokenType;
        }

        @Override
        public String toString() {
            return "ResultAsk{" +
                    "accessToken='" + accessToken + '\'' +
                    ", scope='" + scope + '\'' +
                    ", tokenType='" + tokenType + '\'' +
                    '}';
        }
    }

    private static class GetHandle implements HttpHandler {

        @Override
        public void handle(HttpExchange t) throws IOException {
            logger.debug("Client GET method");
            File index = new File("index.html");
            byte[] indexBytes = Files.readAllBytes(index.toPath());
            logger.debug("Read all bytes from index.html ");

            String response = new String(indexBytes, StandardCharsets.UTF_8);

            t.sendResponseHeaders(200,response.length());
            t.getResponseHeaders().add("Content-Type","text/html; charset=UTF-8");

            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
            logger.debug("End GET Method.");
        }
    }

    private record PostHandle(HttpAuthServer httpAuthServer) implements HttpHandler {
        @Override
            public void handle(HttpExchange exchange) throws IOException {
                logger.debug("POST /processData");
                InputStream inputStream = exchange.getRequestBody();
                logger.debug("Get requestBody");
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String requestBody = reader.readLine();
                reader.close();

                logger.debug("RequestBody -> " + requestBody);

                this.httpAuthServer.setResultAsk(new ObjectMapper().readValue(requestBody, ResultAsk.class));
                logger.info("Created ResultAsk for HttpAuthServer");

                String response = "OK";
                exchange.sendResponseHeaders(200, response.getBytes().length);
                logger.debug("Send response Headers 200");
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
                logger.debug("END POST /processData");

                exchange.getHttpContext().getServer().stop(0);
                logger.info("HTTP Server - is stop");
            }
        }

    private static class FaviconHandle implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            logger.debug("Client want get favicon.ico");
            exchange.sendResponseHeaders(204,-1);
            exchange.close();
            logger.debug("We send to client what we dont have it");
        }
    }
}