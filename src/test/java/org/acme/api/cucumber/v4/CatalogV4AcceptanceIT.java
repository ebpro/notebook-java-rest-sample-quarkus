package org.acme.api.cucumber.v4;

import io.quarkiverse.cucumber.CucumberOptions;
import io.quarkiverse.cucumber.CucumberQuarkusTest;
import io.quarkus.test.common.QuarkusTestResource;
import org.acme.testresources.PostgreSqlTestResource;

@QuarkusTestResource(PostgreSqlTestResource.class)
@CucumberOptions(features = {
                "classpath:features/catalog",
                "classpath:features/v4"
}, tags = "@v4", glue = "org.acme.api.cucumber.steps")
public class CatalogV4AcceptanceIT extends CucumberQuarkusTest {
}
