package ua.wyverno.twitch.api.authorization;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpAuthServer {

    //DEFAULT VARIABLES
    private static final int DEFAULT_PORT = 2828;
    private HttpServer httpServer;

    public HttpAuthServer() throws IOException {
        this(DEFAULT_PORT);
    }
    public HttpAuthServer(int port) throws IOException {
        this.httpServer = HttpServer.create(new InetSocketAddress(port),0);
    }

    public void start() {

    }
}

