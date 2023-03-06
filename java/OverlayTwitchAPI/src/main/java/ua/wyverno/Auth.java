package ua.wyverno;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class Auth {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(2828), 0);
        server.createContext("/", new GetHandler());
        server.createContext("/processData",new PostHandler());
        server.createContext("/favicon.ico",new FaviconHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port 2828");
    }

    static class GetHandler implements HttpHandler {

        private static int _TRY = 0;

        @Override
        public void handle(HttpExchange t) throws IOException {
            File file = new File("index.html");
            byte[] bytes = Files.readAllBytes(file.toPath());
            String response = new String(bytes, StandardCharsets.UTF_8);
            response = response.replaceAll("\\{try}",String.valueOf(++_TRY));
            System.out.println("Create response");
            t.sendResponseHeaders(200, response.length());
            t.getResponseHeaders().add("Content-Type","text/html; charset=UTF-8");
            t.getResponseHeaders().add("Cache-Control","no-cache");
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    static class PostHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            InputStream inputStream = exchange.getRequestBody();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            System.out.println(reader.readLine());
            reader.close();
            String response = "OK";
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    static class FaviconHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.sendResponseHeaders(204,-1);
            exchange.close();
        }
    }
}
