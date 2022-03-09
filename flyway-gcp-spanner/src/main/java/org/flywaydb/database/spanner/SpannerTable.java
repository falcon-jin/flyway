package org.flywaydb.database.spanner;

import org.flywaydb.core.internal.database.base.Table;
import org.flywaydb.core.internal.jdbc.JdbcTemplate;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SpannerTable extends Table<SpannerDatabase, SpannerSchema> {

    public SpannerTable(JdbcTemplate jdbcTemplate, SpannerDatabase database, SpannerSchema schema, String name) {
        super(jdbcTemplate, database, schema, name);
    }

    @Override
    protected boolean doExists() throws SQLException {
        try (Connection c = database.getNewRawConnection()) {
            Statement s = c.createStatement();
            s.close();
            try (ResultSet tables = c.getMetaData().getTables("", "", this.name, null)) {
                return tables.next();
            }
        }
    }

    @Override
    protected void doLock() {}

    @Override
    protected void doDrop() throws SQLException {
        try (Statement statement = jdbcTemplate.getConnection().createStatement()) {
            statement.execute("DROP TABLE " + database.quote(name));
        }
    }

    @Override
    public String toString() {
        return database.quote(name);
    }
}