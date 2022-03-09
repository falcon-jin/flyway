package org.flywaydb.core.internal.database.saphana;

import org.flywaydb.core.internal.database.base.Connection;
import org.flywaydb.core.internal.database.base.Schema;
import org.flywaydb.core.internal.exception.FlywaySqlException;

import java.sql.SQLException;

public class SAPHANAConnection extends Connection<SAPHANADatabase> {
    private final boolean isCloud;

    SAPHANAConnection(SAPHANADatabase database, java.sql.Connection connection) {
        super(database, connection);
        try {
            String build = jdbcTemplate.queryForString("SELECT VALUE FROM M_HOST_INFORMATION WHERE KEY='build_branch'");
            // Cloud databases will be fa/CE<year>.<build> eg. fa/CE2020.48
            // On-premise will be fa/hana<version>sp<servicepack> eg. fa/hana2sp05
            isCloud = build.startsWith("fa/CE");
        } catch (SQLException e) {
            throw new FlywaySqlException("Unable to determine build edition", e);
        }
    }

    public boolean isCloudConnection() {
        return isCloud;
    }

    @Override
    protected String getCurrentSchemaNameOrSearchPath() throws SQLException {
        return jdbcTemplate.queryForString("SELECT CURRENT_SCHEMA FROM DUMMY");
    }

    @Override
    public void doChangeCurrentSchemaOrSearchPathTo(String schema) throws SQLException {
        jdbcTemplate.execute("SET SCHEMA " + database.doQuote(schema));
    }

    @Override
    public Schema getSchema(String name) {
        return new SAPHANASchema(jdbcTemplate, database, name);
    }
}