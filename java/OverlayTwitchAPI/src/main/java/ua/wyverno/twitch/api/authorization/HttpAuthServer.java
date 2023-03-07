package ua.wyverno.twitch.api.authorization;

import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.util.ExceptionToString;

import java.awt.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.net.URL;

public class HttpAuthServer {

    private static final Logger logger = LoggerFactory.getLogger(HttpAuthServer.class);

    //DEFAULT VARIABLES
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
    }

    public void start() {
        this.isRunServer = true;
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
        return this.resultAsk;
    }
    public static class ResultAsk {

    }
}

