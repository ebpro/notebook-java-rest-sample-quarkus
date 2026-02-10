package org.acme.api.v2;

import org.acme.domain.Product;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.ArrayList;
import java.util.List;

/**
 * REST resource for managing products in memory.
 * *
 * <p>
 * Learning Objectives:
 * <ul>
 * <li>Introduction of sub-resources using path parameters.</li>
 * <li>Exception-based error handling (Standard JAX-RS exceptions).</li>
 * <li>CRUD operations basics (GET, POST, DELETE).</li>
 * </ul>
 */
@Path("/api/v2/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Product V2", description = "Memory-based API with Path Params and Exceptions")
public class ProductResourceV2 {

    /**
     * Shared in-memory storage.
     */
    private static final List<Product> products = new ArrayList<>();

    /**
     * Retrieves all products from memory.
     */
    @GET
    @Operation(summary = "List all products")
    public List<Product> getAllProducts() {
        return products;
    }

    /**
     * Retrieves a specific product by its SKU.
     * Pedagogical Note: We use Response.status().entity() to ensure the
     * error message is actually sent in the HTTP body.
     */
    @GET
    @Path("/{sku}")
    @Operation(summary = "Get product by SKU")
    @APIResponse(responseCode = "200", description = "Product found")
    @APIResponse(responseCode = "404", description = "Product not found")
    public Product getProductBySku(
            @Parameter(description = "Product SKU", required = true) @PathParam("sku") String sku) {
        return products.stream()
                .filter(p -> p.sku().equals(sku))
                .findFirst()
                .orElseThrow(() -> new WebApplicationException(
                        Response.status(Response.Status.NOT_FOUND)
                                .entity("Product not found") // Body content
                                .type(MediaType.TEXT_PLAIN)
                                .build()));
    }

    /**
     * Deletes a product by SKU.
     */
    @DELETE
    @Path("/{sku}")
    @Operation(summary = "Delete product")
    @APIResponse(responseCode = "204", description = "Deleted")
    @APIResponse(responseCode = "404", description = "Not found")
    public Response deleteProduct(@PathParam("sku") String sku) {
        boolean removed = products.removeIf(p -> p.sku().equals(sku));
        if (!removed) {
            // Explicitly returning the message body
            throw new WebApplicationException(
                    Response.status(Response.Status.NOT_FOUND)
                            .entity("Product not found")
                            .type(MediaType.TEXT_PLAIN)
                            .build());
        }
        return Response.noContent().build();
    }

    /**
     * Creates a new product.
     * * @param product Product to create.
     *
     * @return 201 Created.
     * @throws WebApplicationException with 409 Conflict if SKU exists.
     */
    @POST
    @Operation(summary = "Create product")
    @APIResponse(responseCode = "201", description = "Created")
    @APIResponse(responseCode = "409", description = "SKU already exists")
    public Response createProduct(Product product) {
        boolean exists = products.stream()
                .anyMatch(p -> p.sku().equals(product.sku()));

        if (exists) {
            // We use WebApplicationException to ensure the body contains the error message
            throw new WebApplicationException(
                    Response.status(Response.Status.CONFLICT)
                            .entity("SKU already exists")
                            .type(MediaType.TEXT_PLAIN)
                            .build());
        }

        products.add(product);
        return Response.status(Response.Status.CREATED).entity(product).build();
    }

    /**
     * Pedagogical helper to clear the list between tests.
     */
    public static void clearProducts() {
        products.clear();
    }
}
