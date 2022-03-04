package com.example.weather.net;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;

/**
 * The type Hi net util.
 */
public class HiNetUtil {

    /**
     * Build params string.
     *
     * @param url    the url
     * @param params the params
     * @return the string
     */
    public static String buildParams(String url, Map<String, String> params) {
        if (params == null) return null;
        StringBuilder builder = new StringBuilder(url);
        boolean isFirst = true;
        for (String key : params.keySet()) {
            String value = params.get(key);
            if (key != null && value != null) {
                if (isFirst) {
                    isFirst = false;
                    builder.append("?");
                } else {
                    builder.append("&");
                }
                builder.append(key)
                        .append("=")
                        .append(value);
            }
        }
        return builder.toString();
    }

    /**
     * Close.
     *
     * @param closeable the closeable
     */
    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
