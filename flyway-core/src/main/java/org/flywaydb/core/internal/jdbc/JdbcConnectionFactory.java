package org.flywaydb.core.internal.jdbc;

import lombok.CustomLog;
import lombok.Getter;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.api.configuration.Configuration;
import org.flywaydb.core.internal.database.DatabaseType;
import org.flywaydb.core.internal.database.DatabaseTypeRegister;
import org.flywaydb.core.internal.exception.FlywaySqlException;

import org.flywaydb.core.internal.util.ExceptionUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * Utility class for dealing with jdbc connections.
 */
@CustomLog
public class JdbcConnectionFactory {
    private final DataSource dataSource;
    private final int connectRetries;
    private final int connectRetriesInterval;
    private final Configuration configuration;
    @Getter
    private final DatabaseType databaseType;
    @Getter
    private final String jdbcUrl;
    @Getter
    private final String driverInfo;
    @Getter
    private final String productName;

    private Connection firstConnection;
    private ConnectionInitializer connectionInitializer;







    /**
     * Creates a new JDBC connection factory. This automatically opens a first connection which can be obtained via
     * a call to getConnection and which must be closed again to avoid leaking it.
     *
     * @param dataSource The DataSource to obtain the connection from.
     * @param configuration The Flyway configuration.
     * @param statementInterceptor The statement interceptor. {@code null} if none.
     */
    public JdbcConnectionFactory(DataSource dataSource, Configuration configuration, StatementInterceptor statementInterceptor) {
        this.dataSource = dataSource;
        this.connectRetries = configuration.getConnectRetries();
        this.connectRetriesInterval = configuration.getConnectRetriesInterval();
        this.configuration = configuration;

        firstConnection = JdbcUtils.openConnection(dataSource, connectRetries, connectRetriesInterval);
        this.databaseType = DatabaseTypeRegister.getDatabaseTypeForConnection(firstConnection);

        final DatabaseMetaData databaseMetaData = JdbcUtils.getDatabaseMetaData(firstConnection);
        this.jdbcUrl = getJdbcUrl(databaseMetaData);
        this.driverInfo = getDriverInfo(databaseMetaData);
        this.productName = JdbcUtils.getDatabaseProductName(databaseMetaData);




        firstConnection = this.databaseType.alterConnectionAsNeeded(firstConnection, configuration);
    }

    public void setConnectionInitializer(ConnectionInitializer connectionInitializer) {
        this.connectionInitializer = connectionInitializer;
    }













    public Connection openConnection() throws FlywayException {
        Connection connection = firstConnection == null ? JdbcUtils.openConnection(dataSource, connectRetries, connectRetriesInterval) : firstConnection;
        firstConnection = null;

        if (connectionInitializer != null) {
            connectionInitializer.initialize(this, connection);
        }












        connection = databaseType.alterConnectionAsNeeded(connection, configuration);
        return connection;
    }

    public interface ConnectionInitializer {
        void initialize(JdbcConnectionFactory jdbcConnectionFactory, Connection connection);
    }

    private static String getJdbcUrl(DatabaseMetaData databaseMetaData) {
        String url;
        try {
            url = databaseMetaData.getURL();
        } catch (SQLException e) {
            throw new FlywaySqlException("Unable to retrieve the JDBC connection URL!", e);
        }
        if (url == null) {
            return "";
        }
        return filterUrl(url);
    }

    /**
     * Filter out URL parameters to avoid including passwords etc.
     */
    static String filterUrl(String url) {
        int questionMark = url.indexOf("?");
        if (questionMark >= 0 && !url.contains("?databaseName=")) {
            url = url.substring(0, questionMark);
        }
        url = url.replaceAll("://.*:.*@", "://");
        return url;
    }

    private static String getDriverInfo(DatabaseMetaData databaseMetaData) {
        try {
            return databaseMetaData.getDriverName() + " " + databaseMetaData.getDriverVersion();
        } catch (SQLException e) {
            throw new FlywaySqlException("Unable to read database driver info: " + e.getMessage(), e);
        }
    }
}