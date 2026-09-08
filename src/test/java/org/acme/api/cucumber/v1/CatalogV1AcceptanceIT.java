package org.acme.api.cucumber.v1;

import io.quarkiverse.cucumber.CucumberQuarkusTest;
import io.quarkus.test.common.QuarkusTestResource;
import org.acme.testresources.PostgreSqlTestResource;
import io.quarkiverse.cucumber.CucumberOptions;

@QuarkusTestResource(PostgreSqlTestResource.class)
@CucumberOptions(features = "classpath:features/v1", glue = {
                "org.acme.api.cucumber.steps",
                "org.acme.api.cucumber.v1"
})
public class CatalogV1AcceptanceIT extends CucumberQuarkusTest {
}
