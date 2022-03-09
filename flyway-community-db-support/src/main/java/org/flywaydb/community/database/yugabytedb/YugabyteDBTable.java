package org.flywaydb.community.database.yugabytedb;

import org.flywaydb.core.internal.database.postgresql.PostgreSQLTable;
import org.flywaydb.core.internal.jdbc.JdbcTemplate;

public class YugabyteDBTable extends PostgreSQLTable {
    /**
     * @param jdbcTemplate The JDBC template for communicating with the DB.
     * @param database The database-specific support.
     * @param schema The schema this table lives in.
     * @param name The name of the table.
     */
    public YugabyteDBTable(JdbcTemplate jdbcTemplate, YugabyteDBDatabase database, YugabyteDBSchema schema, String name) {
        super(jdbcTemplate, database, schema, name);
    }
}