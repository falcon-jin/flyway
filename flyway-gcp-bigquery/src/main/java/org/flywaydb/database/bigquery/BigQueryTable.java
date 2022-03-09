package org.flywaydb.database.bigquery;

import org.flywaydb.core.internal.database.InsertRowLock;
import org.flywaydb.core.internal.database.base.Table;
import org.flywaydb.core.internal.jdbc.JdbcTemplate;

import java.sql.SQLException;

public class BigQueryTable extends Table<BigQueryDatabase, BigQuerySchema> {
    private final InsertRowLock insertRowLock;

    BigQueryTable(JdbcTemplate jdbcTemplate, BigQueryDatabase database, BigQuerySchema schema, String name) {
        super(jdbcTemplate, database, schema, name);
        this.insertRowLock = new InsertRowLock(jdbcTemplate, 10);
    }

    @Override
    protected void doDrop() throws SQLException {
        jdbcTemplate.execute("DROP TABLE " + database.quote(schema.getName(), name));
    }

    @Override
    protected boolean doExists() throws SQLException {
        if (!schema.exists()) {
            return false;
        }
        return jdbcTemplate.queryForInt(
                "SELECT COUNT(table_name) FROM " + database.quote(schema.getName()) + ".INFORMATION_SCHEMA.TABLES WHERE table_type='BASE TABLE' AND table_name=?", name) > 0;
    }

    @Override
    protected void doLock() throws SQLException {
        String updateLockStatement = "UPDATE " + this + " SET installed_on = CURRENT_TIMESTAMP() WHERE version = '?' AND DESCRIPTION = 'flyway-lock'";
        String deleteExpiredLockStatement =
                " DELETE FROM " + this +
                        " WHERE DESCRIPTION = 'flyway-lock'" +
                        " AND installed_on < TIMESTAMP '?'";

        if (lockDepth == 0) {
            insertRowLock.doLock(database.getInsertStatement(this), updateLockStatement, deleteExpiredLockStatement, database.getBooleanTrue());
        }
    }

    @Override
    protected void doUnlock() throws SQLException {
        if (lockDepth == 1) {
            insertRowLock.doUnlock(getDeleteLockTemplate());
        }
    }

    private String getDeleteLockTemplate() {
        return "DELETE FROM " + this + " WHERE version = '?' AND DESCRIPTION = 'flyway-lock'";
    }
}