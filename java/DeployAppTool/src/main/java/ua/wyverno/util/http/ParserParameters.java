package ua.wyverno.util.http;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ParserParameters {

    private static final String QUERY_ENCODING = "UTF-8";

    /**
     * Extract and decode query parameters from a URI and return them as
     * a map.
     *
     * @param uri The uri from which the parameters should be parsed.
     * @return The map containing the result.
     */
    public static Map<String, String> getParameters(URI uri) {
        return parseQuery(uri.getRawQuery());
    }

    /**
     * Extract and decode query parameters from an encoded query string
     *
     * @param query The raw query string of a URI or URL.
     * @return The map containing the result.
     */
    public static Map<String, String> parseQuery(String query) {
        if (query == null || query.isEmpty())
            return Collections.emptyMap();

        Map<String, String> result = new HashMap<String, String>();
        int top = query.length();
        int start = 0;
        for (int idx = 0; idx < top; ++idx) {
            char c = query.charAt(idx);
            if (c == '&') {
                parsePair(result, query.substring(start, idx));
                start = idx + 1;
            }
        }
        if (start < top)
            parsePair(result, query.substring(start));
        return result;
    }

    private static void parsePair(Map<String, String> result, String pair) {
        int sep = pair.indexOf('=');
        if (sep < 0)
            result.put(decode(pair), null);
        else
            result.put(decode(pair.substring(0, sep)), decode(pair.substring(sep + 1)));
    }

    /**
     * Decode a value using the URLDecoder and UTF-8
     *
     * @param value The value to decode
     * @return The decoded value.
     */
    public static String decode(String value) {
        try {
            return URLDecoder.decode(value, QUERY_ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }

}

