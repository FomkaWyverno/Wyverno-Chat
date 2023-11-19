package ua.wyverno.twitch.api.chat.elements;

import ua.wyverno.util.ResourceLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Цей об'єкт потрібен для завантаження<BR/>
 * Шаблонів HTML елементів, та отримання їх
 */
public class ElementResourceManager {
    private static ElementResourceManager instance;

    private final Map<String,String> htmlTemplateMap = new HashMap<>();
    private ElementResourceManager() {}

    public static ElementResourceManager getInstance() {
        if (instance == null) {
            instance = new ElementResourceManager();
        }
        return instance;
    }

    public String getTemplate(String path) {
        if (this.htmlTemplateMap.containsKey(path)) {

            return this.htmlTemplateMap.get(path);
        }
        AtomicReference<String> template = new AtomicReference<>();
        try {
            ResourceLoader.getResourceAsBytes(path)
                    .ifPresentOrElse(bytes -> {
                        template.set(new String(bytes, StandardCharsets.UTF_8));
                    }, () -> {
                        template.set("Fail to load HTML Template Path: " + path);
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.htmlTemplateMap.put(path, template.get());

        return template.get();
    }
}
