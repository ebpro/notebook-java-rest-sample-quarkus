package org.acme.api;

import org.acme.persistence.ProductEntity;
import org.acme.service.ProductService;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

/**
 * Resource REST v3 avec ProductService orchestrant le repository.
 *
 * Points pédagogiques :
 * - Introduction de la couche service pour orchestrer la persistance
 * - La ressource REST ne gère plus directement EntityManager ou la liste
 * mémoire
 * - Injection via constructeur (@Inject)
 * - Gestion des endpoints CRUD avec HTTP codes standards :
 * - GET /products
 * - GET /products/{sku}
 * - POST /products
 * - DELETE /products/{sku}
 *
 */
@Path("/api/v3/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class ProductResourceV3 {

    /**
     * Service qui orchestre les opérations métier et persistance.
     * Remarquez que nous injectons le service via le constructeur.
     */
    private final ProductService service;

    /**
     * Constructeur avec injection CDI.
     * Quarkus se charge d’injecter l’instance de ProductService.
     *
     * @param service Service métier orchestrant le repository
     */
    @Inject
    public ProductResourceV3(ProductService service) {
        this.service = service;
    }

    /**
     * GET /api/v3/products
     *
     * Retourne tous les produits présents en base via le service.
     *
     * @return Liste de ProductEntity
     */
    @GET
    public List<ProductEntity> getAll() {
        return service.getAll();
    }

    /**
     * GET /api/v3/products/{sku}
     *
     * Récupère un produit spécifique par SKU.
     * HTTP 404 Not Found si le produit n'existe pas.
     *
     * @param sku Identifiant unique du produit
     * @return ProductEntity correspondant
     */
    @GET
    @Path("/{sku}")
    public ProductEntity getBySku(@PathParam("sku") String sku) {
        return service.getBySku(sku);
    }

    /**
     * POST /api/v3/products
     *
     * Crée un nouveau produit.
     * HTTP 201 Created si succès, 409 Conflict si SKU existant.
     *
     * @param product Produit à créer
     * @return Response HTTP
     */
    @POST
    public Response create(ProductEntity product) {
        ProductEntity created = service.create(product);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    /**
     * DELETE /api/v3/products/{sku}
     *
     * Supprime un produit par SKU.
     * HTTP 204 No Content si succès, 404 Not Found si produit inexistant.
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
