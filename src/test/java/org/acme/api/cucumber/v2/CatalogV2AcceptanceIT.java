package org.acme.api.cucumber.v2;

import io.quarkiverse.cucumber.CucumberQuarkusTest;
import io.quarkus.test.common.QuarkusTestResource;
import org.acme.testresources.PostgreSqlTestResource;

import io.quarkiverse.cucumber.CucumberOptions;

@QuarkusTestResource(PostgreSqlTestResource.class)
@CucumberOptions(features = "classpath:features/v2", glue = "org.acme.api.cucumber.steps")
public class CatalogV2AcceptanceIT extends CucumberQuarkusTest {
}
