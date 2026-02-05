package org.acme.api.cucumber;

import io.quarkiverse.cucumber.CucumberQuarkusTest;
import io.quarkus.test.common.QuarkusTestResource;
import org.acme.testresources.PostgreSqlTestResource;

import io.quarkiverse.cucumber.CucumberOptions;

@QuarkusTestResource(PostgreSqlTestResource.class)
@CucumberOptions(features = "classpath:features/v2")
public class ProductV2CucumberIT extends CucumberQuarkusTest {
}
