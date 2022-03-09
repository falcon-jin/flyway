package org.flywaydb.community.database.yugabytedb;

import org.flywaydb.core.internal.database.base.Schema;
import org.flywaydb.core.internal.database.postgresql.PostgreSQLConnection;

public class YugabyteDBConnection extends PostgreSQLConnection {

    YugabyteDBConnection(YugabyteDBDatabase database, java.sql.Connection connection) {
        super(database, connection);
    }

    @Override
    public Schema getSchema(String name) {
        return new YugabyteDBSchema(jdbcTemplate, (YugabyteDBDatabase) database, name);
    }
}