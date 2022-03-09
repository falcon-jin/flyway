package org.flywaydb.core.internal.license;

import lombok.Getter;

@Getter
public enum Edition {
    COMMUNITY("Community"),
    PRO("Teams"),
    ENTERPRISE("Teams"),
    TIER3("Tier 3")



    ;

    private final String description;

    Edition(String name) {
        this.description = "Flyway " + name + " Edition";
    }

    @Override
    public String toString() {
        return description;
    }
}