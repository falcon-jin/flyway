package org.flywaydb.core.internal.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.*;
import java.nio.charset.Charset;

/**
 * Utility class for copying files and their contents. Inspired by Spring's own.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileCopyUtils {
    /**
     * Copy the contents of the given Reader into a String.
     * Closes the reader when done.
     *
     * @param in the reader to copy from
     * @return the String that has been copied to
     * @throws IOException in case of I/O errors
     */
    public static String copyToString(Reader in) throws IOException {
        StringWriter out = new StringWriter();
        copy(in, out);
        String str = out.toString();

        //Strip UTF-8 BOM if necessary
        if (str.startsWith("\ufeff")) {
            return str.substring(1);
        }

        return str;
    }

    /**
     * Copy the contents of the given InputStream into a new String based on this encoding.
     * Closes the stream when done.
     *
     * @param in the stream to copy from
     * @param encoding The encoding to use.
     * @return The new String.
     * @throws IOException in case of I/O errors
     */
    public static String copyToString(InputStream in, Charset encoding) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
        copy(in, out);
        return out.toString(encoding.name());
    }

    /**
     * Copy the contents of the given Reader to the given Writer.
     * Closes both when done.
     *
     * @param in the Reader to copy from
     * @param out the Writer to copy to
     * @throws IOException in case of I/O errors
     */
    public static void copy(Reader in, Writer out) throws IOException {
        try {
            char[] buffer = new char[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            out.flush();
        } finally {
            IOUtils.close(in);
            IOUtils.close(out);
        }
    }

    /**
     * Copy the contents of the given InputStream to the given OutputStream.
     * Closes both streams when done.
     *
     * @param in the stream to copy from
     * @param out the stream to copy to
     * @return the number of bytes copied
     * @throws IOException in case of I/O errors
     */
    public static int copy(InputStream in, OutputStream out) throws IOException {
        try {
            int byteCount = 0;
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                byteCount += bytesRead;
            }
            out.flush();
            return byteCount;
        } finally {
            IOUtils.close(in);
            IOUtils.close(out);
        }
    }
}