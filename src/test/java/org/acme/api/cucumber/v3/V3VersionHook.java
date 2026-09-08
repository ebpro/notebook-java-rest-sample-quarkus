package org.acme.api.cucumber.v3;

import io.cucumber.java.Before;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import org.acme.api.cucumber.VersionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pins the API version to v3 for every scenario of {@code CatalogV3AcceptanceIT}.
 * Being part of this IT class's glue only, it runs deterministically before each
 * of its scenarios and is the single place that sets the version for this class.
 */
@Dependent
public class V3VersionHook {
    private static final Logger LOGGER = LoggerFactory.getLogger(V3VersionHook.class);

    @Inject
    VersionContext versionContext;

    @Before
    public void pinVersion() {
        LOGGER.info("Setting API Version to v3 for this acceptance test class");
        versionContext.setVersion("v3");
    }
}
