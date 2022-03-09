package org.flywaydb.core.internal.resolver.java;

import org.flywaydb.core.api.MigrationType;
import org.flywaydb.core.api.migration.JavaMigration;
import org.flywaydb.core.internal.resolver.ResolvedMigrationImpl;
import org.flywaydb.core.internal.util.ClassUtils;

public class ResolvedJavaMigration extends ResolvedMigrationImpl {
    /**
     * Creates a new ResolvedJavaMigration based on this JavaMigration.
     *
     * @param javaMigration The JavaMigration to use.
     */
    public ResolvedJavaMigration(JavaMigration javaMigration) {
        super(javaMigration.getVersion(),
              javaMigration.getDescription(),
              javaMigration.getClass().getName(),
              javaMigration.getChecksum(),
              null,




                              MigrationType.JDBC,
              ClassUtils.getLocationOnDisk(javaMigration.getClass()),
              new JavaMigrationExecutor(javaMigration)
             );
    }
}