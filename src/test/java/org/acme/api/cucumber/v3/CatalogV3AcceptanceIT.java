package org.acme.api.cucumber.v3;

import io.quarkiverse.cucumber.CucumberOptions;
import io.quarkiverse.cucumber.CucumberQuarkusTest;
import io.quarkus.test.common.QuarkusTestResource;
import org.acme.testresources.PostgreSqlTestResource;

@QuarkusTestResource(PostgreSqlTestResource.class)
@CucumberOptions(features = {
                "classpath:features/catalog",
                "classpath:features/v3"
}, tags = "@v3", glue = "org.acme.api.cucumber.steps")
public class CatalogV3AcceptanceIT extends CucumberQuarkusTest {
}
