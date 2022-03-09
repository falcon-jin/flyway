package org.flywaydb.core.internal.license;

import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.internal.util.FlywayDbWebsiteLinks;

public class FlywayTrialExpiredException extends FlywayException {
    public FlywayTrialExpiredException(Edition edition) {
        super("Your 30 day limited Flyway trial license has expired and is no longer valid. " +
                      "Visit " +
                      FlywayDbWebsiteLinks.TRIAL_UPGRADE +
                      " to upgrade to a full " + edition + " license to keep on using this software.");
    }
}