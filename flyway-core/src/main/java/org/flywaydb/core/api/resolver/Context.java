package org.flywaydb.core.api.resolver;

import org.flywaydb.core.api.configuration.Configuration;

import java.sql.Connection;

/**
 * The context relevant to a migration resolver.
 */
public interface Context {
    /**
     * @return The configuration currently in use.
     */
    Configuration getConfiguration();
}