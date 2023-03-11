package ua.wyverno.twitch.api.chat;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.util.ExceptionToString;

import java.net.InetSocketAddress;

public class ChatWebSocketServer extends WebSocketServer {

    public ChatWebSocketServer(int port) {
        super(new InetSocketAddress(port));
    }

    private static final Logger logger = LoggerFactory.getLogger(ChatWebSocketServer.class);

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        logger.info("WebSocket opened: " + webSocket.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        logger.info("WebSocket closed: " + webSocket.getRemoteSocketAddress());
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        logger.info("WebSocket message received: " + s);
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        logger.info("WebSocket error: " + ExceptionToString.getString(e));
    }

    @Override
    public void onStart() {

    }
}
