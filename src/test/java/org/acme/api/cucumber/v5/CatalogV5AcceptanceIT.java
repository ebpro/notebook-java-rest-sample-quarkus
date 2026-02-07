package org.acme.api.cucumber.v5;

import io.quarkiverse.cucumber.CucumberOptions;
import io.quarkiverse.cucumber.CucumberQuarkusTest;
import io.quarkus.test.common.QuarkusTestResource;
import org.acme.testresources.PostgreSqlTestResource;

@QuarkusTestResource(PostgreSqlTestResource.class)
@CucumberOptions(features = {
                "classpath:features/catalog",
                "classpath:features/v5"
}, tags = "@v5", glue = "org.acme.api.cucumber.steps")
public class CatalogV5AcceptanceIT extends CucumberQuarkusTest {
}
