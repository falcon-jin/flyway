package org.flywaydb.core.internal.license;

import org.flywaydb.core.internal.util.FlywayDbWebsiteLinks;

public class FlywayTeamsUpgradeMessage {
    public static String generate(String detectedFeature, String usageMessage) {
        return "Detected " + detectedFeature + ". " +
                "Upgrade to " + Edition.ENTERPRISE + " to " + usageMessage + ". Try " + Edition.ENTERPRISE + " " +
                "for free: " + FlywayDbWebsiteLinks.TRY_TEAMS_EDITION;
    }
}