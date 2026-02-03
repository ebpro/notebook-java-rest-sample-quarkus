package org.acme.api;

import org.acme.dto.CreateProductRequest;
import org.acme.dto.ProductDTO;
import org.acme.service.ProductServiceV4;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

/**
 * Resource REST v4 utilisant les DTOs et ProductServiceV4.
 *
 * Points pédagogiques :
 * - Séparation claire entre entité JPA et DTO exposé à l'API.
 * - La ressource REST ne manipule jamais ProductEntity directement.
 * - Création d'un DTO spécifique pour la création (CreateProductRequest) et
 * pour la réponse (ProductDTO).
 * - Le service V4 fait le mapping entre entités et DTOs.
 */
@Path("/api/v4/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class ProductResourceV4 {

    /**
     * Service métier orchestrant les opérations et le mapping entité → DTO.
     */
    private final ProductServiceV4 service;

    /**
     * Constructeur avec injection CDI.
     *
     * @param service Service métier
     */
    @Inject
    public ProductResourceV4(ProductServiceV4 service) {
        this.service = service;
    }

    /**
     * GET /api/v4/products
     * Retourne tous les produits sous forme de DTO.
     */
    @GET
    public List<ProductDTO> getAll() {
        return service.getAll();
    }

    /**
     * GET /api/v4/products/{sku}
     * Retourne un produit spécifique par SKU sous forme de DTO.
     * HTTP 404 si le produit n'existe pas.
     *
     * @param sku Identifiant unique du produit
     * @return ProductDTO
     */
    @GET
    @Path("/{sku}")
    public ProductDTO getBySku(@PathParam("sku") String sku) {
        return service.getBySku(sku);
    }

    /**
     * POST /api/v4/products
     * Crée un produit à partir d'un DTO de création.
     * HTTP 201 Created si succès.
     *
     * @param request DTO contenant les données du nouveau produit
     * @return ProductDTO créé
     */
    @POST
    public Response create(CreateProductRequest request) {
        ProductDTO created = service.create(request);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    /**
     * DELETE /api/v4/products/{sku}
     * Supprime un produit par SKU.
     * HTTP 204 No Content si succès, HTTP 404 si inexistant.
     *
     * @param sku Identifiant du produit à supprimer
     * @return Response HTTP
     */
    @DELETE
    @Path("/{sku}")
    public Response delete(@PathParam("sku") String sku) {
        service.delete(sku);
        return Response.noContent().build();
    }
}
