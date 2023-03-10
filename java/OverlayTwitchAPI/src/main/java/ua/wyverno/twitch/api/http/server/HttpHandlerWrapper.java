package ua.wyverno.twitch.api.http.server;

import com.sun.net.httpserver.HttpHandler;

public class HttpHandlerWrapper {

    private final String PATH;

    private final HttpHandler HANDLER;

    public HttpHandlerWrapper(String path, HttpHandler handler) {
        this.PATH = path;
        this.HANDLER = handler;
    }

    public String getPATH() {
        return PATH;
    }

    public HttpHandler getHANDLER() {
        return HANDLER;
    }
}
