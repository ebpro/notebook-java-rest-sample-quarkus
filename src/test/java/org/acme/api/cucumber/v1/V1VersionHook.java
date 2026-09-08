package org.acme.api.cucumber.v1;

import io.cucumber.java.Before;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import org.acme.api.cucumber.VersionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pins the API version to v1 for every scenario of {@code CatalogV1AcceptanceIT}.
 * Being part of this IT class's glue only, it runs deterministically before each
 * of its scenarios and is the single place that sets the version for this class.
 */
@Dependent
public class V1VersionHook {
    private static final Logger LOGGER = LoggerFactory.getLogger(V1VersionHook.class);

    @Inject
    VersionContext versionContext;

    @Before
    public void pinVersion() {
        LOGGER.info("Setting API Version to v1 for this acceptance test class");
        versionContext.setVersion("v1");
    }
}
