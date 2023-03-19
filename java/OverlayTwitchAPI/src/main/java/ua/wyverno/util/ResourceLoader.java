package ua.wyverno.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;

public class ResourceLoader {

    private static final Logger logger = LoggerFactory.getLogger(ResourceLoader.class);

    public static Optional<byte[]> getResourceAsBytes(String path) throws IOException {
        path = getCorrectPath(path);

        URL url = ResourceLoader.class.getResource(path);
        InputStream is = ResourceLoader.class.getResourceAsStream(path);

        logger.trace("Resource URL is null? " + (url == null));
        logger.trace("InputStream Resource is null? " + (is == null));

        if (url != null) {
            logger.debug("Resource URL toString() -> " + url);
        }

        if (is != null) {
            logger.trace("Read all bytes for resource");
            byte[] bytes = is.readAllBytes();
            return Optional.ofNullable(bytes);
        }

        return Optional.empty();
    }

    private static String getCorrectPath(String path) {
        logger.trace("getCorrectPath() path = " + path);
        path = path.replace("\\","/");
        logger.trace("path replaced \\ to /");
        if (!path.startsWith("/")) {
            logger.trace("path not start with /");
            path = "/" + path;
        }

        logger.trace("Path return = " + path);
        return path;
    }
}
