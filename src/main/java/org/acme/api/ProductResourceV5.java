package org.acme.api;

import org.acme.dto.CreateProductRequest;
import org.acme.dto.ProductDTO;
import org.acme.service.ProductServiceV5;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

/**
 * Resource REST v5 utilisant :
 * - DTOs pour séparer les entités de l'API
 * - ProductServiceV5 qui utilise PanacheRepository pour la persistance
 *
 * Points pédagogiques :
 * 1. Les endpoints REST n'ont pas de logique métier.
 * 2. La ressource REST ne manipule jamais ProductEntity directement.
 * 3. Le service orchestrant (ProductServiceV5) fait le mapping Entity ↔ DTO et
 * utilise Panache pour CRUD.
 * 4. Injection par constructeur pour testabilité et cohérence.
 * 5. Extension naturelle de la V4 : seuls le repository et le service changent.
 * 6. Les méthodes de base du repository sont générées par Panache.
 */
@Path("/api/v5/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class ProductResourceV5 {

    /**
     * Service métier orchestrant les opérations et le mapping entité → DTO.
     * Implémente PanacheRepository pour simplifier la persistance.
     */
    private final ProductServiceV5 service;

    /**
     * Injection du service par constructeur
     * CDI de Quarkus gère la création et l’injection
     */
    @Inject
    public ProductResourceV5(ProductServiceV5 service) {
        this.service = service;
    }

    /**
     * GET /api/v5/products
     *
     * @return Liste de ProductDTO
     */
    @GET
    public List<ProductDTO> getAll() {
        return service.getAll();
    }

    /**
     * GET /api/v5/products/{sku}
     *
     * @param sku SKU du produit
     * @return ProductDTO correspondant
     */
    @GET
    @Path("/{sku}")
    public ProductDTO getBySku(@PathParam("sku") String sku) {
        return service.getBySku(sku);
    }

    /**
     * POST /api/v5/products
     *
     * @param request DTO contenant les infos pour créer le produit
     * @return ProductDTO créé avec HTTP 201
     */
    @POST
    public Response create(CreateProductRequest request) {
        ProductDTO created = service.create(request);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    /**
     * DELETE /api/v5/products/{sku}
     *
     * @param sku SKU du produit à supprimer
     * @return HTTP 204 si succès, 404 si inexistant
     */
    @DELETE
    @Path("/{sku}")
    public Response delete(@PathParam("sku") String sku) {
        service.delete(sku);
        return Response.noContent().build();
    }
}
