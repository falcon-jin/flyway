package org.flywaydb.core.internal.sqlscript;

/**
 * Executor for SQL scripts.
 */
public interface SqlScriptExecutor {
    /**
     * Executes this SQL script.
     *
     * @param sqlScript The SQL script.
     */
    void execute(SqlScript sqlScript);
}