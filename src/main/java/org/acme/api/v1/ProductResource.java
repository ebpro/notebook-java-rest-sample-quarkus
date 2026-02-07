package org.acme.api.v1;

import org.acme.domain.Product;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.ArrayList;
import java.util.List;

/**
 * Minimal REST resource managing products in memory.
 *
 * <p>
 * Learning Objectives:
 * <ul>
 * <li>Understand JAX-RS annotations (@Path, @GET, @POST).</li>
 * <li>Manual HTTP response building (201 Created, 409 Conflict).</li>
 * <li>Handling state with a static collection (Request-scoped lifecycle).</li>
 * </ul>
 */
@Path("/api/v1/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Product V1", description = "Basic memory-based API for M1 introduction")
public class ProductResource {

    /**
     * In-memory storage shared across all requests.
     */
    private static final List<Product> products = new ArrayList<>();

    /**
     * Retrieves all products currently stored in memory.
     *
     * @return a list of products.
     */
    @GET
    @Operation(summary = "Get all products", description = "Returns the full list from memory")
    public List<Product> getAllProducts() {
        return products;
    }

    /**
     * Creates a new product if the SKU does not already exist.
     *
     * @param product the product object to persist.
     * @return 201 Created with the product, or 409 Conflict if SKU is taken.
     */
    @POST
    @Operation(summary = "Create product", description = "Adds a product if SKU is unique")
    public Response createProduct(Product product) {
        // Business logic: check for SKU uniqueness using anyMatch for better
        // readability
        boolean exists = products.stream()
                .anyMatch(p -> p.sku().equals(product.sku()));

        if (exists) {
            // Conflict scenario: Return 409 and a descriptive message
            return Response.status(Response.Status.CONFLICT)
                    .entity("SKU already exists")
                    .build();
        }

        // Add to memory
        products.add(product);

        // Success scenario: Return 201 and the created object
        return Response.status(Response.Status.CREATED)
                .entity(product)
                .build();
    }

    /**
     * Pedagogical helper to reset the state between test scenarios.
     */
    public static void clearProducts() {
        products.clear();
    }
}
