package org.flywaydb.core.internal.license;

import lombok.AccessLevel;
import lombok.CustomLog;
import lombok.NoArgsConstructor;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.internal.util.FileCopyUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@CustomLog
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VersionPrinter {
    public static final String VERSION = readVersion();
    public static Edition EDITION =

            Edition.COMMUNITY




            ;

    public static String getVersion() {
        return VERSION;
    }

    public static void printVersion() {
        printVersionOnly();
        LOG.info("See what's new here: https://flywaydb.org/documentation/learnmore/releaseNotes#" + VERSION);
        LOG.info("");
    }

    public static void printVersionOnly() {
        LOG.info(EDITION + " " + VERSION + " by Redgate");
    }

    private static String readVersion() {
        try {
            return FileCopyUtils.copyToString(
                    VersionPrinter.class.getClassLoader().getResourceAsStream("org/flywaydb/core/internal/version.txt"),
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new FlywayException("Unable to read Flyway version: " + e.getMessage(), e);
        }
    }
}