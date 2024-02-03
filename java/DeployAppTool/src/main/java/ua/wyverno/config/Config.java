package ua.wyverno.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.dropbox.auth.DropBoxAuthServer;

import java.awt.*;
import java.awt.desktop.SystemEventListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class Config {

    private static final Logger logger = LoggerFactory.getLogger(Config.class);

    private final Path pathConfig;

    private final Path pathApplication;
    private String accessTokenDropBox;

    public Config(Path path) throws IOException {
        this.pathConfig = path;
        if (this.pathConfig.toFile().exists()) {
            Properties properties = new Properties();
            properties.load(Files.newBufferedReader(this.pathConfig));
            String strPath = properties.getProperty("pathApplication");

            if (strPath != null && !strPath.isEmpty()) {
                this.pathApplication = Paths.get(strPath);
            } else {
                throw new IllegalArgumentException("Value for Path Application is empty! Editing your config file");
            }

            if (properties.containsKey("accessTokenDropBox")) {
                this.accessTokenDropBox = properties.getProperty("accessTokenDropBox");
            } else {
                this.accessTokenDropBox = "";
            }
        } else {
            throw new FileNotFoundException("Setting-file not exists!");
        }
    }

    public static void createDefaultPropertiesFile(Path path) throws IOException {
        Properties properties = new Properties();
        properties.put("pathApplication","");
        properties.store(Files.newBufferedWriter(path), "");
    }

    public Path getPathApplication() {
        return pathApplication;
    }

    public String getAccessTokenDropBox() {
        return this.accessTokenDropBox;
    }

    public void setAccessTokenDropBox(String accessTokenDropBox) throws IOException {
        this.accessTokenDropBox = accessTokenDropBox;
        this.storeConfig();
    }

    private void storeConfig() throws IOException {
        Properties properties = new Properties();
        properties.put("pathApplication",this.pathApplication.toString());
        properties.put("accessTokenDropBox", this.accessTokenDropBox);
        properties.store(Files.newBufferedWriter(this.pathConfig), "");
    }

    public void updateDbxAccessToken() throws IOException {
        DropBoxAuthServer dropBoxAuthServer = new DropBoxAuthServer();

        dropBoxAuthServer.askAuthorizationDropBox();
        Desktop.getDesktop().browse(URI.create("https://www.dropbox.com/oauth2/authorize?client_id=dt6pyeq6flawbn0&response_type=code&redirect_uri=http://localhost:3737/"));

        this.setAccessTokenDropBox(dropBoxAuthServer.getAccessToken());
        logger.trace("Set Access Token DropBox");
        dropBoxAuthServer.stopServer();
        logger.debug("Update config! Change accessToken");
        this.storeConfig();
    }
}
