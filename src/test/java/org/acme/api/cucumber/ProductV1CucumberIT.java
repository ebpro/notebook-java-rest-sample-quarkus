package org.acme.api.cucumber;

import io.quarkiverse.cucumber.CucumberQuarkusTest;
import io.quarkus.test.common.QuarkusTestResource;
import org.acme.testresources.PostgreSqlTestResource;
import io.quarkiverse.cucumber.CucumberOptions;

@QuarkusTestResource(PostgreSqlTestResource.class)
@CucumberOptions(features = "classpath:features/v1")
public class ProductV1CucumberIT extends CucumberQuarkusTest {
}
