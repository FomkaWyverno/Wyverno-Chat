package ua.wyverno.twitch.api.http.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.twitch.api.authorization.ResultAsk;
import ua.wyverno.twitch.api.authorization.http.handlers.GetHandle;
import ua.wyverno.twitch.api.authorization.http.handlers.PostHandle;
import ua.wyverno.twitch.api.http.server.handlers.CloseHandle;
import ua.wyverno.twitch.api.http.server.handlers.FaviconHandle;
import ua.wyverno.twitch.api.http.server.handlers.main.page.MainHandle;
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

    private volatile ResultAsk resultAsk;

    public HttpServer() throws IOException {
        this(DEFAULT_PORT);
    }
    public HttpServer(int port) throws IOException {
        this.httpServer = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress(port),0);

        this.httpServer.createContext("/access",new GetHandle()); // Создаэмо контексти для серверу.
        this.httpServer.createContext("/processData",new PostHandle(this));
        this.httpServer.createContext("/favicon.ico",new FaviconHandle());
        this.httpServer.createContext("/close",new CloseHandle());
        this.httpServer.createContext("/",new MainHandle());
    }

    public void start() { // Запускаэмо сервер
        this.isRunServer = true;
        logger.info("HTTP Server is starting on port " + this.httpServer.getAddress().getPort());
        this.httpServer.start();
    }

    public boolean isRunServer() {
        return isRunServer;
    }

    public void askAuthorization(String url) { // Робим запит на авторизацію за допомоги браузеру по дефолту.
        try {
            Desktop.getDesktop().browse(new URL(url).toURI());
        } catch (IOException | URISyntaxException e) {
            logger.error(ExceptionToString.getString(e));
        }
    }

    public void setResultAsk(ResultAsk resultAsk) { // Встановлюємо результат того що прийшло нам після авторизації
        this.resultAsk = resultAsk;
        logger.debug("Result ask we got. Notify all threads.");
        synchronized (this.lockObject) {
            lockObject.notifyAll();
        }
    }

    public ResultAsk getResultAsk() throws InterruptedException { // Відаємо результат авторизації якщо нема результата потік який питається взяти цей
        // результат замре поки не зявится результат.
        if (this.resultAsk == null) {
            logger.debug("Result ask = null, so we wait it.");
        }

        synchronized (this.lockObject) {
            while (this.resultAsk == null) {
                lockObject.wait();
            }
        }
        return resultAsk;
    }
}