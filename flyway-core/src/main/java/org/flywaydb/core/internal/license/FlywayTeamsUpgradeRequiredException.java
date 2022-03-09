package org.flywaydb.core.internal.license;

import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.internal.util.FlywayDbWebsiteLinks;

/**
 * Thrown when an attempt was made to use a Flyway Teams Edition feature not supported by
 * Flyway Community Edition.
 */
public class FlywayTeamsUpgradeRequiredException extends FlywayException {
    public FlywayTeamsUpgradeRequiredException(String feature) {
        super(Edition.ENTERPRISE + " upgrade required: " + feature + " is not supported by " + Edition.COMMUNITY + "\n" +
                      "Try " + Edition.ENTERPRISE + " for free: " + FlywayDbWebsiteLinks.TRY_TEAMS_EDITION);
    }
}