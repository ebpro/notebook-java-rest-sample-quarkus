package org.acme.api.v1;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.common.http.TestHTTPEndpoint;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@TestHTTPEndpoint(ProductResourceV1.class)
@DisplayName("ProductResource v1 - In-Memory Tests")
class ProductResourceV1IT {

    private Map<String, Object> newProduct(String sku) {
        return Map.of(
                "sku", sku,
                "name", "MemoryItem",
                "price", 12.34,
                "stock", 3);
    }

    @Test
    void shouldCreateAndRetrieveProductInMemory() {
        String sku = "IT-V1-" + UUID.randomUUID();

        // --- Create product ---
        given()
                .contentType("application/json")
                .body(newProduct(sku))
                .when()
                .post()
                .then()
                .statusCode(201)
                .body("sku", equalTo(sku))
                .body("name", equalTo("MemoryItem"))
                .body("price", equalTo(12.34f))
                .body("stock", equalTo(3));

        // --- Verify product exists in list ---
        given()
                .when()
                .get()
                .then()
                .statusCode(200)
                .body("sku", hasItem(sku));
    }
}
