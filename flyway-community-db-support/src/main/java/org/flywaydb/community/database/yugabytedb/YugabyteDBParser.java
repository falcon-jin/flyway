package org.flywaydb.community.database.yugabytedb;

import org.flywaydb.core.api.configuration.Configuration;
import org.flywaydb.core.internal.database.postgresql.PostgreSQLParser;
import org.flywaydb.core.internal.parser.ParsingContext;

public class YugabyteDBParser extends PostgreSQLParser {
    protected YugabyteDBParser(Configuration configuration, ParsingContext parsingContext) {
        super(configuration, parsingContext);
    }
}