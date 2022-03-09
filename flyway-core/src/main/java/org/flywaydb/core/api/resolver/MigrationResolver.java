package org.flywaydb.core.api.resolver;

import java.util.Collection;

/**
 * Resolves available migrations. This interface can be implemented to create custom resolvers. A custom resolver
 * can be used to create additional types of migrations not covered by the standard resolvers (jdbc, sql, spring-jdbc).
 * Using the skipDefaultResolvers configuration property, the built-in resolvers can also be completely replaced.
 */
public interface MigrationResolver {
    /**
     * Resolves the available migrations.
     *
     * @return The available migrations.
     */
    Collection<ResolvedMigration> resolveMigrations(Context context);
}