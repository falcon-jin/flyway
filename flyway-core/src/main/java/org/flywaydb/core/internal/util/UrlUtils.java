package org.flywaydb.core.internal.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flywaydb.core.api.FlywayException;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

/**
 * Collection of utility methods for working with URLs.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UrlUtils {
    /**
     * Retrieves the file path of this URL, with any trailing slashes removed.
     *
     * @param url The URL to get the file path for.
     * @return The file path.
     */
    public static String toFilePath(URL url) {
        String filePath = new File(decodeURL(url.getPath().replace("+", "%2b"))).getAbsolutePath();
        if (filePath.endsWith("/")) {
            return filePath.substring(0, filePath.length() - 1);
        }
        return filePath;
    }

    /**
     * Decodes this UTF-8 encoded URL.
     *
     * @param url The url to decode.
     * @return The decoded URL.
     */
    public static String decodeURL(String url) {
        try {
            return URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Can never happen", e);
        }
    }
}