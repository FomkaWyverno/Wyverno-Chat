package ua.wyverno.twitch.api.http.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.twitch.api.authorization.http.handlers.GetHandle;
import ua.wyverno.twitch.api.authorization.http.handlers.PostHandle;
import ua.wyverno.twitch.api.http.server.handlers.FaviconHandle;
import ua.wyverno.util.ExceptionToString;

import java.awt.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.net.URL;

public class HttpServer {

    private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);

    //DEFAULT VARIABLES
    private final    Object lockObject = new Object();
    private static final int DEFAULT_PORT = 2828;
    private final com.sun.net.httpserver.HttpServer httpServer;
    private boolean isRunServer = false;
    private static final String authURL =
    "https://id.twitch.tv/oauth2/authorize?client_id=znxb14or3tj0cm6e1pixh7zijlsgua&redirect_uri=http%3A%2F%2Flocalhost%3A2828/access&response_type=token&scope=channel%3Aread%3Aredemptions+chat%3Aread";

    private ResultAsk resultAsk = null;

    public HttpServer() throws IOException {
        this(DEFAULT_PORT);
    }
    public HttpServer(int port) throws IOException {
        this.httpServer = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress(port),0);

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

    public void setResultAsk(ResultAsk resultAsk) {
        this.resultAsk = resultAsk;
        logger.debug("Result Ask to set -> " + resultAsk.toString());
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
}