package ua.wyverno.dropbox.auth.handlers;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class HttpAppTokensHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode mainNode = JsonNodeFactory.instance.objectNode();
        mainNode.put("redirect_uri","/accesstoken");
        mainNode.put("client_id","dt6pyeq6flawbn0");
        mainNode.put("client_secret","vs8isg971kczqyi");

        String json = mapper.writeValueAsString(mainNode);

        exchange.getResponseHeaders().add("Content-Type", "text/json");
        exchange.sendResponseHeaders(200,json.getBytes().length);

        exchange.getResponseBody().write(json.getBytes());

        exchange.close();
    }
}
