package org.acme.api.v3;

import org.acme.persistence.ProductEntity;
import org.acme.service.v3.ProductServiceV3;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

/**
 * REST resource v3 using a Service for orchestration and persistence.
 *
 * <p>
 * Learning Objectives:
 * <ul>
 * <li>Introduction of the Service layer to encapsulate business logic.</li>
 * <li>Dependency Injection (CDI) using the {@code @Inject} constructor.</li>
 * <li>Decoupling the HTTP boundary from the persistence implementation.</li>
 * </ul>
 */
@Path("/api/v3/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Product V3", description = "Service-based API with Dependency Injection")
@RequestScoped
public class ProductResource {

    private final ProductServiceV3 service;

    /**
     * Constructor injection.
     * Quarkus automatically provides an instance of ProductService.
     *
     * @param service Business service orchestrating the repository.
     */
    @Inject
    public ProductResource(ProductServiceV3 service) {
        this.service = service;
    }

    /**
     * Retrieves all products via the service.
     */
    @GET
    @Operation(summary = "Get all products")
    public List<ProductEntity> getAll() {
        return service.getAll();
    }

    /**
     * Retrieves a specific product.
     *
     * @param sku Unique identifier.
     * @return The product entity.
     * @throws WebApplicationException (e.g. 404) if delegated by the service.
     */
    @GET
    @Path("/{sku}")
    @Operation(summary = "Get product by SKU")
    @APIResponse(responseCode = "200", description = "Product found")
    @APIResponse(responseCode = "404", description = "Product not found")
    public ProductEntity getBySku(
            @Parameter(description = "Product SKU", required = true) @PathParam("sku") String sku) {
        return service.getBySku(sku);
    }

    /**
     * Delegates product creation to the service.
     *
     * @param product Entity to persist.
     * @return 201 Created with the persisted entity.
     */
    @POST
    @Operation(summary = "Create product")
    @APIResponse(responseCode = "201", description = "Created")
    @APIResponse(responseCode = "409", description = "Conflict - SKU already exists")
    public Response create(ProductEntity product) {
        ProductEntity created = service.create(product);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    /**
     * Removes a product by SKU.
     *
     * @param sku Identifier of the product to delete.
     * @return 204 No Content.
     */
    @DELETE
    @Path("/{sku}")
    @Operation(summary = "Delete product")
    @APIResponse(responseCode = "204", description = "Deleted")
    @APIResponse(responseCode = "404", description = "Not found")
    public Response delete(@PathParam("sku") String sku) {
        service.delete(sku);
        return Response.noContent().build();
    }
}
