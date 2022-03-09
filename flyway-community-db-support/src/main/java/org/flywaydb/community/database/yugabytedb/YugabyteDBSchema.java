package org.flywaydb.community.database.yugabytedb;

import org.flywaydb.core.internal.database.base.Table;
import org.flywaydb.core.internal.database.postgresql.PostgreSQLSchema;
import org.flywaydb.core.internal.jdbc.JdbcTemplate;

public class YugabyteDBSchema extends PostgreSQLSchema {
    /**
     * @param jdbcTemplate The Jdbc Template for communicating with the DB.
     * @param database The database-specific support.
     * @param name The name of the schema.
     */
    public YugabyteDBSchema(JdbcTemplate jdbcTemplate, YugabyteDBDatabase database, String name) {
        super(jdbcTemplate, database, name);
    }

    @Override
    public Table getTable(String tableName) {
        return new YugabyteDBTable(jdbcTemplate, (YugabyteDBDatabase) database, this, tableName);
    }
}