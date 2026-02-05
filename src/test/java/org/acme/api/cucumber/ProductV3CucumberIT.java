package org.acme.api.cucumber;

import io.quarkiverse.cucumber.CucumberOptions;
import io.quarkiverse.cucumber.CucumberQuarkusTest;
import io.quarkus.test.common.QuarkusTestResource;
import org.acme.testresources.PostgreSqlTestResource;

@QuarkusTestResource(PostgreSqlTestResource.class)
@CucumberOptions(features = {
        "classpath:features/catalog",
        "classpath:features/v3"
}, tags = "@v3")
public class ProductV3CucumberIT extends CucumberQuarkusTest {
}
