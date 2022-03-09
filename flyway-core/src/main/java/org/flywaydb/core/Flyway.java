package org.flywaydb.core;

import lombok.CustomLog;
import lombok.experimental.ExtensionMethod;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.api.MigrationInfoService;
import org.flywaydb.core.api.callback.Event;
import org.flywaydb.core.api.configuration.ClassicConfiguration;
import org.flywaydb.core.api.configuration.Configuration;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.flywaydb.core.api.exception.FlywayValidateException;
import org.flywaydb.core.api.logging.LogFactory;
import org.flywaydb.core.api.output.*;
import org.flywaydb.core.api.resolver.MigrationResolver;
import org.flywaydb.core.internal.FlywayTeamsObjectResolver;
import org.flywaydb.core.internal.callback.CallbackExecutor;
import org.flywaydb.core.internal.command.*;
import org.flywaydb.core.internal.database.base.Database;
import org.flywaydb.core.internal.database.base.Schema;
import org.flywaydb.core.internal.jdbc.StatementInterceptor;
import org.flywaydb.core.internal.schemahistory.SchemaHistory;
import org.flywaydb.core.internal.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 这是 Flyway 的中心点，对于大多数用户来说，这是他们唯一需要处理的课程。
 *
 * 它是公共 API，可以从中调用所有重要的 Flyway 功能，例如 clean、validate 和 migrate。
 *
 * 要开始，您需要做的就是创建一个已配置的 Flyway 对象，然后调用其主体方法。
 * <pre>
 * Flyway flyway = Flyway.configure().dataSource(url, user, password).load();
 * flyway.migrate();
 * </pre>
 * 请注意，配置的 Flyway 对象是不可变的。如果您更改配置，您最终将创建一个新的 Flyway 对象。
 */
@CustomLog
@ExtensionMethod(FlywayTeamsObjectResolver.class)
public class Flyway {
    private final ClassicConfiguration configuration;
    private final FlywayExecutor flywayExecutor;

    /**
     * 这是你的起点。这将创建一个配置，可以在使用 load() 方法将其加载到新的 Flyway 实例之前根据您的需要进行自定义。
     *
     *在最简单的形式中，这是您使用所有默认值配置 Flyway 以开始使用的方式：
     * <pre>Flyway flyway = Flyway.configure().dataSource(url, user, password).load();</pre>
     *
     * 之后，您将拥有一个完全配置的 Flyway 实例，可用于调用 Flyway
     * functionality such as migrate() or clean().
     *
     * @return A new configuration from which Flyway can be loaded.
     */
    public static FluentConfiguration configure() {
        return new FluentConfiguration();
    }

    /**
     * 这是你的起点。这将创建一个配置，可以在使用 load() 方法将其加载到新的 Flyway 实例之前根据您的需要进行自定义。
     *
     * 在最简单的形式中，这是您使用所有默认值配置 Flyway 以开始使用的方式：
     * <pre>Flyway flyway = Flyway.configure().dataSource(url, user, password).load();</pre>
     *
     * After that you have a fully-configured Flyway instance at your disposal which can be used to invoke Flyway
     * functionality such as migrate() or clean().
     *
     * @param classLoader The class loader to use when loading classes and resources.
     * @return A new configuration from which Flyway can be loaded.
     */
    public static FluentConfiguration configure(ClassLoader classLoader) {
        return new FluentConfiguration(classLoader);
    }

    /**
     * Creates a new instance of Flyway with this configuration. In general the Flyway.configure() factory method should
     * be preferred over this constructor, unless you need to create or reuse separate Configuration objects.
     *
     * @param configuration The configuration to use.
     */
    public Flyway(Configuration configuration) {
        this.configuration = new ClassicConfiguration(configuration);
        // Load callbacks from default package
        this.configuration.loadCallbackLocation("db/callback", false);
        this.flywayExecutor = new FlywayExecutor(this.configuration);

        LogFactory.setConfiguration(this.configuration);
    }

    /**
     * @return The configuration that Flyway is using.
     */
    public Configuration getConfiguration() {
        return new ClassicConfiguration(configuration);
    }

    /**
     * Starts the database migration. All pending migrations will be applied in order.
     * Calling migrate on an up-to-date database has no effect.
     * <img src="https://flywaydb.org/assets/balsamiq/command-migrate.png" alt="migrate">
     *
     * @return An object summarising the successfully applied migrations.
     * @throws FlywayException when the migration failed.
     */
    public MigrateResult migrate() throws FlywayException {
        return flywayExecutor.execute(new FlywayExecutor.Command<MigrateResult>() {
            public MigrateResult execute(MigrationResolver migrationResolver, SchemaHistory schemaHistory, Database database,
                                         Schema defaultSchema, Schema[] schemas, CallbackExecutor callbackExecutor, StatementInterceptor statementInterceptor) {
                if (configuration.isValidateOnMigrate()) {
                    ValidateResult validateResult = doValidate(database, migrationResolver, schemaHistory, defaultSchema, schemas, callbackExecutor, true);
                    if (!validateResult.validationSuccessful && !configuration.isCleanOnValidationError()) {
                        throw new FlywayValidateException(validateResult.errorDetails, validateResult.getAllErrorMessages());
                    }
                }

                if (!schemaHistory.exists()) {
                    List<Schema> nonEmptySchemas = new ArrayList<>();
                    for (Schema schema : schemas) {
                        if (schema.exists() && !schema.empty()) {
                            nonEmptySchemas.add(schema);
                        }
                    }

                    if (!nonEmptySchemas.isEmpty()



                    ) {
                        if (configuration.isBaselineOnMigrate()) {
                            doBaseline(schemaHistory, callbackExecutor, database);
                        } else {
                            // Second check for MySQL which is sometimes flaky otherwise
                            if (!schemaHistory.exists()) {
                                throw new FlywayException("Found non-empty schema(s) "
                                                                  + StringUtils.collectionToCommaDelimitedString(nonEmptySchemas)
                                                                  + " but no schema history table. Use baseline()"
                                                                  + " or set baselineOnMigrate to true to initialize the schema history table.");
                            }
                        }
                    } else {
                        if (configuration.isCreateSchemas()) {
                            new DbSchemas(database, schemas, schemaHistory, callbackExecutor).create(false);
                        } else if (!defaultSchema.exists()) {
                            LOG.warn("The configuration option 'createSchemas' is false.\n" +
                                             "However, the schema history table still needs a schema to reside in.\n" +
                                             "You must manually create a schema for the schema history table to reside in.\n" +
                                             "See https://flywaydb.org/documentation/concepts/migrations.html#the-createschemas-option-and-the-schema-history-table");
                        }

                        schemaHistory.create(false);
                    }
                }

                MigrateResult result = new DbMigrate(database, schemaHistory, defaultSchema, migrationResolver, configuration, callbackExecutor).migrate();

                callbackExecutor.onOperationFinishEvent(Event.AFTER_MIGRATE_OPERATION_FINISH, result);

                return result;
            }
        }, true);
    }

    /**
     * Retrieves the complete information about all the migrations including applied, pending and current migrations with
     * details and status.
     * <img src="https://flywaydb.org/assets/balsamiq/command-info.png" alt="info">
     *
     * @return All migrations sorted by version, oldest first.
     * @throws FlywayException when the info retrieval failed.
     */
    public MigrationInfoService info() {
        return flywayExecutor.execute(new FlywayExecutor.Command<MigrationInfoService>() {
            public MigrationInfoService execute(MigrationResolver migrationResolver, SchemaHistory schemaHistory, Database database,
                                                Schema defaultSchema, Schema[] schemas, CallbackExecutor callbackExecutor, StatementInterceptor statementInterceptor) {
                MigrationInfoService migrationInfoService = new DbInfo(migrationResolver, schemaHistory, configuration, database, callbackExecutor, schemas).info();

                callbackExecutor.onOperationFinishEvent(Event.AFTER_INFO_OPERATION_FINISH, migrationInfoService.getInfoResult());

                return migrationInfoService;
            }
        }, true);
    }

    /**
     * Drops all objects (tables, views, procedures, triggers, ...) in the configured schemas.
     * The schemas are cleaned in the order specified by the {@code schemas} property.
     * <img src="https://flywaydb.org/assets/balsamiq/command-clean.png" alt="clean">
     *
     * @return An object summarising the actions taken
     * @throws FlywayException when the clean fails.
     */
    public CleanResult clean() {
        return flywayExecutor.execute(new FlywayExecutor.Command<CleanResult>() {
            public CleanResult execute(MigrationResolver migrationResolver, SchemaHistory schemaHistory, Database database,
                                       Schema defaultSchema, Schema[] schemas, CallbackExecutor callbackExecutor, StatementInterceptor statementInterceptor) {
                CleanResult cleanResult = doClean(database, schemaHistory, defaultSchema, schemas, callbackExecutor);

                callbackExecutor.onOperationFinishEvent(Event.AFTER_CLEAN_OPERATION_FINISH, cleanResult);

                return cleanResult;
            }
        }, false);
    }

    /**
     * Validate applied migrations against resolved ones (on the filesystem or classpath)
     * to detect accidental changes that may prevent the schema(s) from being recreated exactly.
     * Validation fails if:
     * <ul>
     * <li>differences in migration names, types or checksums are found</li>
     * <li>versions have been applied that aren't resolved locally anymore</li>
     * <li>versions have been resolved that haven't been applied yet</li>
     * </ul>
     *
     * <img src="https://flywaydb.org/assets/balsamiq/command-validate.png" alt="validate">
     *
     * @throws FlywayException when the validation failed.
     */
    public void validate() throws FlywayException {
        flywayExecutor.execute(new FlywayExecutor.Command<Void>() {
            public Void execute(MigrationResolver migrationResolver, SchemaHistory schemaHistory, Database database,
                                Schema defaultSchema, Schema[] schemas, CallbackExecutor callbackExecutor, StatementInterceptor statementInterceptor) {
                ValidateResult validateResult = doValidate(database, migrationResolver, schemaHistory, defaultSchema, schemas, callbackExecutor, configuration.isIgnorePendingMigrations());

                callbackExecutor.onOperationFinishEvent(Event.AFTER_VALIDATE_OPERATION_FINISH, validateResult);

                LOG.notice("Automate migration testing for Database CI with Flyway Hub. Visit https://flywaydb.org/get-started-with-hub");

                if (!validateResult.validationSuccessful && !configuration.isCleanOnValidationError()) {
                    throw new FlywayValidateException(validateResult.errorDetails, validateResult.getAllErrorMessages());
                }

                return null;
            }
        }, true);
    }

    /**
     * Validate applied migrations against resolved ones (on the filesystem or classpath)
     * to detect accidental changes that may prevent the schema(s) from being recreated exactly.
     * Validation fails if:
     * <ul>
     * <li>differences in migration names, types or checksums are found</li>
     * <li>versions have been applied that aren't resolved locally anymore</li>
     * <li>versions have been resolved that haven't been applied yet</li>
     * </ul>
     *
     * <img src="https://flywaydb.org/assets/balsamiq/command-validate.png" alt="validate">
     *
     * @return An object summarising the validation results
     * @throws FlywayException when the validation failed.
     */
    public ValidateResult validateWithResult() throws FlywayException {
        return flywayExecutor.execute(new FlywayExecutor.Command<ValidateResult>() {
            public ValidateResult execute(MigrationResolver migrationResolver, SchemaHistory schemaHistory, Database database,
                                          Schema defaultSchema, Schema[] schemas, CallbackExecutor callbackExecutor, StatementInterceptor statementInterceptor) {
                ValidateResult validateResult = doValidate(database, migrationResolver, schemaHistory, defaultSchema, schemas, callbackExecutor, configuration.isIgnorePendingMigrations());

                callbackExecutor.onOperationFinishEvent(Event.AFTER_VALIDATE_OPERATION_FINISH, validateResult);

                return validateResult;
            }
        }, true);
    }

    /**
     * Baselines an existing database, excluding all migrations up to and including baselineVersion.
     *
     * <img src="https://flywaydb.org/assets/balsamiq/command-baseline.png" alt="baseline">
     *
     * @return An object summarising the actions taken
     * @throws FlywayException when the schema baseline failed.
     */
    public BaselineResult baseline() throws FlywayException {
        return flywayExecutor.execute(new FlywayExecutor.Command<BaselineResult>() {
            public BaselineResult execute(MigrationResolver migrationResolver, SchemaHistory schemaHistory, Database database,
                                          Schema defaultSchema, Schema[] schemas, CallbackExecutor callbackExecutor, StatementInterceptor statementInterceptor) {
                if (configuration.isCreateSchemas()) {
                    new DbSchemas(database, schemas, schemaHistory, callbackExecutor).create(true);
                } else {
                    LOG.warn("The configuration option 'createSchemas' is false.\n" +
                                     "Even though Flyway is configured not to create any schemas, the schema history table still needs a schema to reside in.\n" +
                                     "You must manually create a schema for the schema history table to reside in.\n" +
                                     "See https://flywaydb.org/documentation/concepts/migrations.html#the-createschemas-option-and-the-schema-history-table");
                }

                BaselineResult baselineResult = doBaseline(schemaHistory, callbackExecutor, database);

                callbackExecutor.onOperationFinishEvent(Event.AFTER_BASELINE_OPERATION_FINISH, baselineResult);

                return baselineResult;
            }
        }, false);
    }

    /**
     * Repairs the Flyway schema history table. This will perform the following actions:
     * <ul>
     * <li>Remove any failed migrations on databases without DDL transactions (User objects left behind must still be cleaned up manually)</li>
     * <li>Realign the checksums, descriptions and types of the applied migrations with the ones of the available migrations</li>
     * </ul>
     * <img src="https://flywaydb.org/assets/balsamiq/command-repair.png" alt="repair">
     *
     * @return An object summarising the actions taken
     * @throws FlywayException when the schema history table repair failed.
     */
    public RepairResult repair() throws FlywayException {
        return flywayExecutor.execute(new FlywayExecutor.Command<RepairResult>() {
            public RepairResult execute(MigrationResolver migrationResolver, SchemaHistory schemaHistory, Database database,
                                        Schema defaultSchema, Schema[] schemas, CallbackExecutor callbackExecutor, StatementInterceptor statementInterceptor) {
                RepairResult repairResult = new DbRepair(database, migrationResolver, schemaHistory, callbackExecutor, configuration).repair();

                callbackExecutor.onOperationFinishEvent(Event.AFTER_REPAIR_OPERATION_FINISH, repairResult);

                return repairResult;
            }
        }, true);
    }

    /**
     * Undoes the most recently applied versioned migration. If target is specified, Flyway will attempt to undo
     * versioned migrations in the order they were applied until it hits one with a version below the target. If there
     * is no versioned migration to undo, calling undo has no effect.
     * <i>Flyway Teams only</i>
     * <img src="https://flywaydb.org/assets/balsamiq/command-undo.png" alt="undo">
     *
     * @return An object summarising the successfully undone migrations.
     * @throws FlywayException when undo failed.
     */
    public UndoResult undo() throws FlywayException {

         throw new org.flywaydb.core.internal.license.FlywayTeamsUpgradeRequiredException("undo");













    }

    private CleanResult doClean(Database database, SchemaHistory schemaHistory, Schema defaultSchema, Schema[] schemas, CallbackExecutor callbackExecutor) {
        return DbClean.class.resolve(database, schemaHistory, defaultSchema, schemas, callbackExecutor, configuration).clean();
    }

    private ValidateResult doValidate(Database database, MigrationResolver migrationResolver, SchemaHistory schemaHistory,
                                      Schema defaultSchema, Schema[] schemas, CallbackExecutor callbackExecutor, boolean ignorePending) {
        ValidateResult validateResult = new DbValidate(database, schemaHistory, defaultSchema, migrationResolver, configuration, ignorePending, callbackExecutor).validate();

        if (!validateResult.validationSuccessful && configuration.isCleanOnValidationError()) {
            doClean(database, schemaHistory, defaultSchema, schemas, callbackExecutor);
        }

        return validateResult;
    }

    private BaselineResult doBaseline(SchemaHistory schemaHistory, CallbackExecutor callbackExecutor, Database database) {
        return new DbBaseline(schemaHistory, configuration.getBaselineVersion(), configuration.getBaselineDescription(), callbackExecutor, database).baseline();
    }
}