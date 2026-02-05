package org.acme;

import io.quarkus.test.junit.QuarkusTest;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@QuarkusTest
@Tag("NFR")
@Disabled
class TechnicalSpecsIT {

  /**
   * Valide [NFR-QA-03] Observability
   * Vérifie que les endpoints de santé Quarkus sont actifs.
   */
  @Test
  void testHealthCheckLiveness() {
    given()
        .when().get("/q/health/live")
        .then()
        .statusCode(200)
        .body("status", equalTo("UP"));
  }

  /**
   * Valide [NFR-SEC-03] Information Leakage
   * On envoie un payload corrompu et on vérifie qu'aucune info sensible ne fuite.
   */
  @Test
  void testNoStacktraceLeakageOnInvalidJson() {
    given()
        .header("Content-Type", "application/json")
        .body("{ \"sku\": \"ERROR-1\", \"price\": \"not-a-number\" }") // Format invalide
        .when()
        .post("/api/v5/products")
        .then()
        .statusCode(anyOf(is(400), is(404), is(422))) // On attend une erreur client
        .body(not(containsString("stacktrace")))
        .body(not(containsString("at io.quarkus")))
        .body(not(containsString("hibernate")))
        .body(not(containsString("exception")));
  }

  /**
   * Valide [NFR-SEC-01] Input Sanitization
   * Teste les prix négatifs ou les stocks incohérents.
   */
  @Test
  void testRejectNegativePrice() {
    given()
        .header("Content-Type", "application/json")
        .body("{ \"sku\": \"NEG-1\", \"name\": \"Bad Price\", \"price\": -10.0, \"stock\": 10 }")
        .when()
        .post("/api/v5/products")
        .then()
        .statusCode(anyOf(is(400), is(422)));
  }

}
