package org.flywaydb.core.internal.resolver.java;

import lombok.RequiredArgsConstructor;
import org.flywaydb.core.api.configuration.Configuration;
import org.flywaydb.core.api.migration.JavaMigration;
import org.flywaydb.core.api.resolver.Context;
import org.flywaydb.core.api.resolver.MigrationResolver;
import org.flywaydb.core.api.resolver.ResolvedMigration;
import org.flywaydb.core.api.ClassProvider;
import org.flywaydb.core.internal.resolver.ResolvedMigrationComparator;
import org.flywaydb.core.internal.util.ClassUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Migration resolver for Java-based migrations. The classes must have a name like R__My_description, V1__Description
 * or V1_1_3__Description.
 */
@RequiredArgsConstructor
public class ScanningJavaMigrationResolver implements MigrationResolver {
    /**
     * The Scanner to use.
     */
    private final ClassProvider<JavaMigration> classProvider;

    /**
     * The configuration to inject (if necessary) in the migration classes.
     */
    private final Configuration configuration;

    @Override
    public List<ResolvedMigration> resolveMigrations(Context context) {
        List<ResolvedMigration> migrations = new ArrayList<>();

        for (Class<?> clazz : classProvider.getClasses()) {
            JavaMigration javaMigration = ClassUtils.instantiate(clazz.getName(), configuration.getClassLoader());
            migrations.add(new ResolvedJavaMigration(javaMigration));
        }

        Collections.sort(migrations, new ResolvedMigrationComparator());
        return migrations;
    }
}