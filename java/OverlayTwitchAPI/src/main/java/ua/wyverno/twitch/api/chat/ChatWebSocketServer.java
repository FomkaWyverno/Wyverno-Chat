package ua.wyverno.twitch.api.chat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.natives.keyboard.NativeKeyboard;
import ua.wyverno.natives.keyboard.NativeKeyboardPressScript;
import ua.wyverno.natives.keyboard.NativeKeyboardReleasedScript;
import ua.wyverno.util.ExceptionToString;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

public class ChatWebSocketServer extends WebSocketServer {

    private static ChatWebSocketServer wssInstance;

    private final static ObjectMapper mapper = new ObjectMapper();
    private final Set<WebSocket> webSocketSet = new HashSet<>();

    private final NativeKeyboard keyboard;

    private ChatWebSocketServer(int port) {
        super(new InetSocketAddress(port));
        this.keyboard = NativeKeyboard.getInstance();
        initialScriptForKeyboard();
    }

    private static final Logger logger = LoggerFactory.getLogger(ChatWebSocketServer.class);

    private void initialScriptForKeyboard() {
        this.keyboard.addScript((NativeKeyboardPressScript) e -> {
            if (e.getKeyCode() == NativeKeyEvent.VC_CONTROL) {
                logger.trace("Press Control");
                this.messageEvent(new Protocol(Protocol.TYPE.pressButton,"Control"));
            }
        });

        this.keyboard.addScript((NativeKeyboardReleasedScript) e -> {
            if (e.getKeyCode() == NativeKeyEvent.VC_CONTROL) {
                logger.trace("Released Control");
                this.messageEvent(new Protocol(Protocol.TYPE.releasedButton, "Control"));
            }
        });
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        logger.info("WebSocket opened: " + webSocket.getRemoteSocketAddress());
        this.webSocketSet.add(webSocket);
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        logger.info("WebSocket closed: " + webSocket.getRemoteSocketAddress());
        this.webSocketSet.remove(webSocket);
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
        logger.info("WebSocket Server is running");
    }

    public void messageEvent(Protocol protocol) {
        this.webSocketSet.forEach(ws -> {
            try {
                ws.send(mapper.writeValueAsString(protocol));
            } catch (JsonProcessingException e) {
                logger.error(ExceptionToString.getString(e));
            }
        });
    }

    public static ChatWebSocketServer getInstance() throws IllegalArgumentException {
        if (wssInstance == null) {
            logger.info("Create Chat Web-Socket-Server on port 2929");
            wssInstance = new ChatWebSocketServer(2929);
            logger.info("Starting Chat Web-Socket-Server");
            wssInstance.start();
        }
        return wssInstance;
    }

    public static void stopWSS() {
        if (wssInstance != null) {
            try {
                logger.info("Try stop Web-Socket-Server");
                wssInstance.stop();
                logger.info("Web-Socket-Server is stop");
                return;
            } catch (InterruptedException e) {
                logger.error(ExceptionToString.getString(e));
            }

        }
        logger.info("Web-Socket-Server null not need stop");
    }
}
