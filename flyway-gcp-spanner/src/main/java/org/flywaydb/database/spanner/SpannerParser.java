package org.flywaydb.database.spanner;

import lombok.CustomLog;
import org.flywaydb.core.api.configuration.Configuration;
import org.flywaydb.core.internal.parser.Parser;
import org.flywaydb.core.internal.parser.ParsingContext;
import org.flywaydb.core.internal.parser.Token;

import java.util.List;

@CustomLog
public class SpannerParser extends Parser {

    public SpannerParser(Configuration configuration, ParsingContext parsingContext) {
        super(configuration, parsingContext, 3);
    }

    @Override
    protected char getIdentifierQuote() {
        return '`';
    }

    protected char getAlternativeIdentifierQuote() {
        return '\"';
    }

    @Override
    protected Boolean detectCanExecuteInTransaction(String simplifiedStatement, List<Token> keywords) {
        LOG.debug("checking if [" + simplifiedStatement + "] can run in transaction");
        // Flyway tries to do hold transaction in which migration will happen
        return false;
    }
}