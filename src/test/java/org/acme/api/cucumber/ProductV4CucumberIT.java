package org.acme.api.cucumber;

import io.quarkiverse.cucumber.CucumberOptions;
import io.quarkiverse.cucumber.CucumberQuarkusTest;
import io.quarkus.test.common.QuarkusTestResource;
import org.acme.testresources.PostgreSqlTestResource;

@QuarkusTestResource(PostgreSqlTestResource.class)
@CucumberOptions(features = {
        "classpath:features/catalog",
        "classpath:features/v4"
}, tags = "@v4")
public class ProductV4CucumberIT extends CucumberQuarkusTest {
}
