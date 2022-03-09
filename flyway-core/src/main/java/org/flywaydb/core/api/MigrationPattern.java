package org.flywaydb.core.api;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MigrationPattern {
    private final String migrationName;

    public boolean matches(MigrationVersion version, String description) {
        if (version != null) {
            String pattern = migrationName.replace("_", ".");
            return pattern.equals(version.toString());
        } else {
            String pattern = migrationName.replace("_", " ");
            return pattern.equals(description);
        }
    }

    @Override
    public String toString() {
        return migrationName;
    }

    @Override
    public int hashCode() {
        return migrationName.hashCode();
    }
}