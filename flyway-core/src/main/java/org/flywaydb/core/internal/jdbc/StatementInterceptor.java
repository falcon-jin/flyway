package org.flywaydb.core.internal.jdbc;

import org.flywaydb.core.api.resource.LoadableResource;
import org.flywaydb.core.internal.database.base.Database;
import org.flywaydb.core.internal.database.base.Table;
import org.flywaydb.core.internal.schemahistory.AppliedMigration;
import org.flywaydb.core.internal.sqlscript.SqlStatement;

import java.util.Map;

public interface StatementInterceptor {
    void init(Database database, Table table);

    void schemaHistoryTableCreate(boolean baseline);

    void schemaHistoryTableInsert(AppliedMigration appliedMigration);

    void close();

    void sqlScript(LoadableResource resource);

    void sqlStatement(SqlStatement statement);

    void interceptCommand(String command);

    void interceptStatement(String sql);

    void interceptPreparedStatement(String sql, Map<Integer, Object> params);

    void interceptCallableStatement(String sql);

    void schemaHistoryTableDeleteFailed(Table table, AppliedMigration appliedMigration);
}