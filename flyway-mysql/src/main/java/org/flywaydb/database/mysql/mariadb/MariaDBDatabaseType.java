package org.flywaydb.database.mysql.mariadb;

import org.flywaydb.core.api.ResourceProvider;
import org.flywaydb.core.api.configuration.Configuration;
import org.flywaydb.core.internal.database.base.Database;
import org.flywaydb.core.internal.database.base.BaseDatabaseType;
import org.flywaydb.database.mysql.MySQLParser;
import org.flywaydb.core.internal.jdbc.JdbcConnectionFactory;
import org.flywaydb.core.internal.jdbc.StatementInterceptor;
import org.flywaydb.core.internal.parser.Parser;
import org.flywaydb.core.internal.parser.ParsingContext;

import java.sql.Connection;
import java.sql.Types;
import java.util.Properties;

public class MariaDBDatabaseType extends BaseDatabaseType {
    @Override
    public String getName() {
        return "MariaDB";
    }

    @Override
    public int getPriority() {
        // Maria needs to be checked in advance of MySql
        return 1;
    }

    @Override
    public int getNullType() {
        return Types.VARCHAR;
    }

    @Override
    public boolean handlesJDBCUrl(String url) {
        if (url.startsWith("jdbc-secretsmanager:mariadb:")) {




            throw new org.flywaydb.core.internal.license.FlywayTeamsUpgradeRequiredException("jdbc-secretsmanager");

        }
        return url.startsWith("jdbc:mariadb:") || url.startsWith("jdbc:p6spy:mariadb:");
    }

    @Override
    public String getDriverClass(String url, ClassLoader classLoader) {





        if (url.startsWith("jdbc:p6spy:mariadb:")) {
            return "com.p6spy.engine.spy.P6SpyDriver";
        }
        return "org.mariadb.jdbc.Driver";
    }

    @Override
    public boolean handlesDatabaseProductNameAndVersion(String databaseProductName, String databaseProductVersion, Connection connection) {
        return databaseProductName.startsWith("MariaDB")
                // Older versions of the driver report MariaDB as "MySQL"
                || (databaseProductName.contains("MySQL") && databaseProductVersion.contains("MariaDB"))
                // Azure Database For MariaDB reports as "MySQL"
                || (databaseProductName.contains("MySQL") && getSelectVersionOutput(connection).contains("MariaDB"));
    }

    @Override
    public Database createDatabase(Configuration configuration, JdbcConnectionFactory jdbcConnectionFactory, StatementInterceptor statementInterceptor) {
        return new MariaDBDatabase(configuration, jdbcConnectionFactory, statementInterceptor);
    }

    @Override
    public Parser createParser(Configuration configuration, ResourceProvider resourceProvider, ParsingContext parsingContext) {
        return new MariaDBParser(configuration, parsingContext);
    }

    @Override
    public void setDefaultConnectionProps(String url, Properties props, ClassLoader classLoader) {
        props.put("connectionAttributes", "program_name:" + APPLICATION_NAME);
    }

    @Override
    public boolean detectPasswordRequiredByUrl(String url) {






        return super.detectPasswordRequiredByUrl(url);
    }
}