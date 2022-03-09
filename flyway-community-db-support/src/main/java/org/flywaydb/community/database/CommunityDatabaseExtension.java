package org.flywaydb.community.database;

import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.extensibility.PluginMetadata;
import org.flywaydb.core.internal.util.FileCopyUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class CommunityDatabaseExtension implements PluginMetadata {
    public String getDescription() {
        return "Community-contributed database support extension " + readVersion() + " by Redgate";
    }

    private static String readVersion() {
        try {
            return FileCopyUtils.copyToString(
                    CommunityDatabaseExtension.class.getClassLoader().getResourceAsStream("org/flywaydb/community/database/version.txt"),
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new FlywayException("Unable to read extension version: " + e.getMessage(), e);
        }
    }
}