package org.flywaydb.community.database.yugabytedb;

import org.flywaydb.core.api.configuration.Configuration;
import org.flywaydb.core.internal.database.postgresql.PostgreSQLDatabase;
import org.flywaydb.core.internal.jdbc.JdbcConnectionFactory;
import org.flywaydb.core.internal.jdbc.StatementInterceptor;

import java.sql.Connection;

public class YugabyteDBDatabase extends PostgreSQLDatabase {

    public YugabyteDBDatabase(Configuration configuration, JdbcConnectionFactory jdbcConnectionFactory, StatementInterceptor statementInterceptor) {
        super(configuration, jdbcConnectionFactory, statementInterceptor);
    }

    @Override
    protected YugabyteDBConnection doGetConnection(Connection connection) {
        return new YugabyteDBConnection(this, connection);
    }

    @Override
    public void ensureSupported() {
        // Checks the Postgres version
        ensureDatabaseIsRecentEnough("11.2");
    }

    @Override
    public boolean supportsDdlTransactions() {
        return false;
    }

}