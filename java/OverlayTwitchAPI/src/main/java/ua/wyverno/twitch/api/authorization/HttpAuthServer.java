package ua.wyverno.twitch.api.authorization;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpAuthServer {

    //DEFAULT VARIABLES
    private static final int DEFAULT_PORT = 2828;
    private HttpServer httpServer;

    private static final String authURL =
    "https://id.twitch.tv/oauth2/authorize?client_id=znxb14or3tj0cm6e1pixh7zijlsgua&redirect_uri=http%3A%2F%2Flocalhost%3A2828/access&response_type=token&scope=channel%3Aread%3Aredemptions+chat%3Aread";


    public HttpAuthServer() throws IOException {
        this(DEFAULT_PORT);
    }
    public HttpAuthServer(int port) throws IOException {
        this.httpServer = HttpServer.create(new InetSocketAddress(port),0);
    }

    public void start() {

    }
}

