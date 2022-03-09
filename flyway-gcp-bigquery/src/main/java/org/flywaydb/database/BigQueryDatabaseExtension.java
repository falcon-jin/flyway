package org.flywaydb.database;

import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.extensibility.PluginMetadata;
import org.flywaydb.core.internal.util.FileCopyUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BigQueryDatabaseExtension implements PluginMetadata {
    public String getDescription() {
        return "GCP BigQuery database support (beta) " + readVersion() + " by Redgate";
    }

    private static String readVersion() {
        try {
            return FileCopyUtils.copyToString(
                    BigQueryDatabaseExtension.class.getClassLoader().getResourceAsStream("org/flywaydb/database/version.txt"),
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new FlywayException("Unable to read extension version: " + e.getMessage(), e);
        }
    }
}