package org.acme.api.cucumber.v5;

import io.cucumber.java.Before;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import org.acme.api.cucumber.VersionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pins the API version to v5 for every scenario of {@code CatalogV5AcceptanceIT}.
 * Being part of this IT class's glue only, it runs deterministically before each
 * of its scenarios and is the single place that sets the version for this class.
 */
@Dependent
public class V5VersionHook {
    private static final Logger LOGGER = LoggerFactory.getLogger(V5VersionHook.class);

    @Inject
    VersionContext versionContext;

    @Before
    public void pinVersion() {
        LOGGER.info("Setting API Version to v5 for this acceptance test class");
        versionContext.setVersion("v5");
    }
}
