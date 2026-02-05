package org.acme.api.client;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.api.client.filters.TestDebugFilter;
import org.acme.dto.CreateProductRequest;
import org.acme.dto.ProductDTO;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

/**
 * REST Client for Product API.
 * configKey "product-api" maps to quarkus.rest-client."product-api".url in
 * properties.
 */
@RegisterRestClient(configKey = "product-api")
@Path("/api/{version}/products")
@RegisterProvider(TestDebugFilter.class)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ProductRestClient {

    // --- TYPED METHODS ---
    // Use these in your actual Application code.
    // They will automatically throw exceptions for 4xx/5xx responses.

    @GET
    Uni<List<ProductDTO>> getAll(@PathParam("version") String version);

    @GET
    @Path("/{sku}")
    Uni<ProductDTO> getBySku(@PathParam("version") String version, @PathParam("sku") String sku);

    // --- RAW METHODS (FOR TESTING) ---
    // Use these in your Cucumber Step Definitions.
    // They return a Response object, allowing you to assert status codes (404, 409,
    // etc.)
    // without the client throwing an exception.

    @GET
    Uni<Response> getAllRaw(@PathParam("version") String version);

    @GET
    @Path("/{sku}")
    Uni<Response> getBySkuRaw(@PathParam("version") String version, @PathParam("sku") String sku);

    @POST
    Uni<Response> createRaw(@PathParam("version") String version, CreateProductRequest request);

    @DELETE
    @Path("/{sku}")
    Uni<Response> deleteRaw(@PathParam("version") String version, @PathParam("sku") String sku);

    // --- Method to send raw JSON body (for testing invalid input) ---
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    Uni<Response> postRawBody(@PathParam("version") String version, String body);
}
