package org.acme.api.v4;

import org.acme.dto.CreateProductRequest;
import org.acme.dto.ProductDTO;
import org.acme.service.v4.ProductServiceV4;

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
 * REST resource v4 demonstrating the Data Transfer Object (DTO) pattern.
 *
 * <p>Learning Objectives:
 * <ul>
 * <li>Decoupling the API contract from the Database Schema (Entities).</li>
 * <li>Using specialized DTOs for input (CreateProductRequest) and output (ProductDTO).</li>
 * <li>Ensuring internal persistence details never leak to the client.</li>
 * </ul>
 */
@Path("/api/v4/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Product V4", description = "API using DTOs to encapsulate the Domain model")
@RequestScoped
public class ProductResourceV4 {

    private final ProductServiceV4 service;

    /**
     * Constructor injection of the service responsible for Entity-DTO mapping.
     *
     * @param service Business service handling V4 logic.
     */
    @Inject
    public ProductResourceV4(ProductServiceV4 service) {
        this.service = service;
    }

    /**
     * Retrieves all products as DTOs.
     */
    @GET
    @Operation(summary = "Get all products (DTO)")
    public List<ProductDTO> getAll() {
        return service.getAll();
    }

    /**
     * Retrieves a specific product DTO by SKU.
     *
     * @param sku Unique identifier.
     * @return Product data transfer object.
     */
    @GET
    @Path("/{sku}")
    @Operation(summary = "Get product by SKU (DTO)")
    @APIResponse(responseCode = "200", description = "Product found")
    @APIResponse(responseCode = "404", description = "Product not found")
    public ProductDTO getBySku(
            @Parameter(description = "Product SKU", required = true) @PathParam("sku") String sku) {
        return service.getBySku(sku);
    }

    /**
     * Creates a product using a specialized request DTO.
     *
     * @param request Data required to create a product.
     * @return 201 Created with the resulting ProductDTO.
     */
    @POST
    @Operation(summary = "Create product (DTO)")
    @APIResponse(responseCode = "201", description = "Created")
    @APIResponse(responseCode = "400", description = "Invalid input")
    @APIResponse(responseCode = "409", description = "SKU already exists")
    public Response create(CreateProductRequest request) {
        ProductDTO created = service.create(request);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    /**
     * Deletes a product by SKU.
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
