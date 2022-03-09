package org.flywaydb.core.internal.database.oracle;

import lombok.CustomLog;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.api.callback.Error;
import org.flywaydb.core.internal.callback.CallbackExecutor;
import org.flywaydb.core.internal.jdbc.*;
import org.flywaydb.core.internal.sqlscript.DefaultSqlScriptExecutor;
import lombok.AccessLevel;
import lombok.Getter;


import org.flywaydb.core.internal.exception.FlywaySqlException;
import org.flywaydb.core.internal.sqlscript.SqlScript;
import org.flywaydb.core.internal.sqlscript.SqlStatement;
import org.flywaydb.core.internal.util.AsciiTable;
import org.flywaydb.core.internal.util.DateUtils;
import org.flywaydb.core.internal.util.StopWatch;
import org.flywaydb.core.internal.util.StringUtils;

import java.io.IOException;
import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@SuppressWarnings("SqlResolve")
@CustomLog
@Getter
public class OracleSqlScriptExecutor extends DefaultSqlScriptExecutor {




















    public OracleSqlScriptExecutor(JdbcTemplate jdbcTemplate,
                                   CallbackExecutor callbackExecutor, boolean undo, boolean batch, boolean outputQueryResults,
                                   StatementInterceptor statementInterceptor) {
        super(jdbcTemplate, callbackExecutor, undo, batch, outputQueryResults, statementInterceptor);
    }















































































































































































































































}