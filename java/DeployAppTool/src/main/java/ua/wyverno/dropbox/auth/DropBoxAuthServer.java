package ua.wyverno.dropbox.auth;

import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.dropbox.auth.handlers.HttpAccessTokenHandler;
import ua.wyverno.dropbox.auth.handlers.HttpAppTokensHandler;
import ua.wyverno.dropbox.auth.handlers.HttpAuthHandler;

import java.io.IOException;
import java.net.InetSocketAddress;

public class DropBoxAuthServer {

    private static final Logger logger = LoggerFactory.getLogger(DropBoxAuthServer.class);

    private HttpServer httpServer;

    private HttpAccessTokenHandler httpAccessTokenHandler = new HttpAccessTokenHandler();
    private static final int DEFAULT_PORT = 3737;

    public DropBoxAuthServer(int port) throws IOException {
        this.httpServer = HttpServer.create(new InetSocketAddress(port), 0);
    }

    public DropBoxAuthServer() throws IOException {
        this(DEFAULT_PORT);
    }

    public void askAuthorizationDropBox() {
        this.httpServer.createContext("/", new HttpAuthHandler());
        this.httpServer.createContext("/tokens", new HttpAppTokensHandler());
        this.httpServer.createContext("/accesstoken", this.httpAccessTokenHandler);
        this.httpServer.start();
        logger.debug("Start http-auth-server port: {}", this.httpServer.getAddress().getPort());
    }

    public String getAccessToken() {
        return this.httpAccessTokenHandler.getAccessToken();
    }

    public void stopServer() {
        logger.trace("Try stop HTTP-Server DropBox Auth");
        this.httpServer.stop(0);
        logger.debug("Stop auth http-server");
    }
}
