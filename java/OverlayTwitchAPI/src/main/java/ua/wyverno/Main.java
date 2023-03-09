package ua.wyverno;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.twitch.api.authorization.AccessTokenNoLongerValidException;
import ua.wyverno.twitch.api.authorization.Authorization;
import ua.wyverno.twitch.api.http.server.HttpServer;
import ua.wyverno.util.ExceptionToString;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            logger.info("Start main class");
            HttpServer httpServer = new HttpServer();
            httpServer.start();
            try {
                new Authorization(httpServer);

                startUI();
            } catch (AccessTokenNoLongerValidException e) {
                logger.error(ExceptionToString.getString(e));
            }

        } catch (Exception e) {
            logger.error(ExceptionToString.getString(e));
        }
    }

    private static int startUI() throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.directory(new File("D:\\MyProgram\\Overlay\\node.js"));
        processBuilder.command("npm.cmd","start");

        Process process = processBuilder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;

        while ((line = reader.readLine()) != null) {
            logger.debug("[Node.js] >>> " + line);
        }

        return process.waitFor();
    }
}
