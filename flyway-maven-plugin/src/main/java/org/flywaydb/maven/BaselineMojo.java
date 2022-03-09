package org.flywaydb.maven;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.flywaydb.core.Flyway;

/**
 * Baselines an existing database, excluding all migrations up to and including baselineVersion.
 */
@Mojo(name = "baseline",
        requiresDependencyResolution = ResolutionScope.TEST,
        defaultPhase = LifecyclePhase.PRE_INTEGRATION_TEST,
        threadSafe = true)
public class BaselineMojo extends AbstractFlywayMojo {
    @Override
    protected void doExecute(Flyway flyway) {
        flyway.baseline();
    }
}