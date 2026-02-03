package org.acme.api;

import org.acme.domain.Product;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Resource REST pour gérer des produits en mémoire
 *
 * Endpoints :
 * - GET /api/v2/products : récupère tous les produits
 * - GET /api/v2/products/{sku} : récupère un produit spécifique par SKU
 * - POST /api/v2/products : crée un produit si SKU unique
 * - DELETE /api/v2/products/{sku} : supprime un produit par SKU
 *
 * Points pédagogiques :
 * - Introduction de GET by SKU et DELETE
 * - Gestion des erreurs via exceptions JAX-RS :
 * - NotFoundException → HTTP 404
 * - Response.Status.CONFLICT → HTTP 409
 * - Stockage en mémoire pour la phase M1
 */
@Path("/api/v2/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductResourceV2 {

    /**
     * Stockage en mémoire partagé pour tous les appels à la ressource.
     */
    private static final List<Product> products = new ArrayList<>();

    /**
     * GET /api/v2/products
     *
     * Retourne tous les produits actuellement en mémoire.
     *
     * @return Liste des produits
     */
    @GET
    public List<Product> getAllProducts() {
        return products;
    }

    /**
     * GET /api/v2/products/{sku}
     *
     * Retourne un produit spécifique identifié par son SKU.
     * Si le produit n'existe pas → HTTP 404 Not Found
     *
     * @param sku Identifiant unique du produit
     * @return Produit correspondant
     */
    @GET
    @Path("/{sku}")
    public Product getProductBySku(@PathParam("sku") String sku) {
        return products.stream()
                .filter(p -> p.sku().equals(sku))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Product not found"));
    }

    /**
     * POST /api/v2/products
     *
     * Ajoute un nouveau produit si le SKU est unique.
     * Retourne :
     * - 201 Created avec le produit créé si succès
     * - 409 Conflict si un produit avec le même SKU existe déjà
     *
     * @param product Produit à créer
     * @return Response HTTP
     */
    @POST
    public Response createProduct(Product product) {
        Optional<Product> existing = products.stream()
                .filter(p -> p.sku().equals(product.sku()))
                .findFirst();

        if (existing.isPresent()) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Product with this SKU already exists")
                    .build();
        }

        products.add(product);
        return Response.status(Response.Status.CREATED).entity(product).build();
    }

    /**
     * DELETE /api/v2/products/{sku}
     *
     * Supprime un produit par son SKU.
     * Retourne :
     * - 204 No Content si succès
     * - 404 Not Found si le produit n'existe pas
     *
     * @param sku Identifiant unique du produit
     * @return Response HTTP
     */
    @DELETE
    @Path("/{sku}")
    public Response deleteProduct(@PathParam("sku") String sku) {
        boolean removed = products.removeIf(p -> p.sku().equals(sku));
        if (!removed) {
            throw new NotFoundException("Product not found");
        }
        return Response.noContent().build();
    }
}
