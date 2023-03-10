package ua.wyverno.twitch.api.http.server;

import com.sun.net.httpserver.HttpHandler;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.util.ExceptionToString;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class HttpServer {

    private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);

    //DEFAULT VARIABLES
    private static final int DEFAULT_PORT = 2828;
    private final com.sun.net.httpserver.HttpServer httpServer;

    public HttpServer() throws IOException {
        this(DEFAULT_PORT);
    }

    public HttpServer(int port) throws IOException {
        this.httpServer = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress(port), 0);

        // Ініцілізуємо обробники HTTP запитів
        List<HttpHandlerWrapper> list = this.findHttpHandlers();

        list.forEach(e -> {
            logger.info("Initialization HTTP Handler PATH: " + e.getPATH());
            this.httpServer.createContext(e.getPATH(),e.getHANDLER());
        });
    }

    public void start() { // Запускаэмо сервер
        logger.info("HTTP Server is starting on port " + this.httpServer.getAddress().getPort());
        this.httpServer.start();
    }

    public void askAuthorization(String url) { // Робим запит на авторизацію за допомоги браузеру по дефолту.
        try {
            Desktop.getDesktop().browse(new URL(url).toURI());
        } catch (IOException | URISyntaxException e) {
            logger.error(ExceptionToString.getString(e));
        }
    }

    private List<HttpHandlerWrapper> findHttpHandlers() {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .forPackage("ua.wyverno")
                .setScanners(Scanners.TypesAnnotated));
        Set<Class<?>> classes = reflections.get(Scanners.TypesAnnotated.of(HttpHandle.class).asClass());

        List<HttpHandlerWrapper> list = new ArrayList<>();

        classes.forEach(element -> {
            logger.debug("Path to class with Annotation HttpHandle -> " + element.getName());
            if (!HttpHandler.class.isAssignableFrom(element)) {
                throw new IllegalArgumentException("Class " + element.getName() + " must be implement HttpHandler interface");
            } else {
                try {
                    HttpHandler httpHandler = (HttpHandler) element.getConstructor().newInstance();
                    logger.info("Create HTTP Handler path -> " + element.getAnnotation(HttpHandle.class).path());
                    list.add(new HttpHandlerWrapper(element.getAnnotation(HttpHandle.class).path(), httpHandler));
                    logger.debug("Added HTTP Handler to list!");
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                         NoSuchMethodException e) {
                    logger.error(ExceptionToString.getString(e));
                }
            }
        });
        logger.trace("Return list with Annotation HttpHandle");
        return list;
    }
}