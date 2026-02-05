package org.acme.api;

import org.acme.domain.Product;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Resource REST minimale pour gérer des produits en mémoire.
 *
 * Caractéristiques : pas de base de données, logique métier simple dans la
 * ressource.
 * Endpoints :
 * - GET /api/v1/products : récupère tous les produits
 * - POST /api/v1/products : crée un nouveau produit si SKU unique
 *
 * Points pédagogiques :
 * - Comprendre la structure d'une ressource JAX-RS
 * - Gestion des codes HTTP (201 Created, 409 Conflict)
 * - Stockage temporaire en mémoire
 */
@Path("/api/v1/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductResourceV1 {

    /**
     * Stockage en mémoire des produits.
     * Statique pour partager la liste entre toutes les instances de la ressource.
     */
    private static final List<Product> products = new ArrayList<>();

    /**
     * GET /api/v1/products
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
     * POST /api/v1/products
     *
     * Ajoute un nouveau produit si le SKU est unique.
     * Retourne :
     * - 201 Created avec le produit créé si succès
     * - 409 Conflict si un produit avec le même SKU existe déjà
     *
     * @param product Produit à créer
     * @return Response HTTP avec statut et entité
     */
    @POST
    public Response createProduct(Product product) {
        // Vérification de l'unicité du SKU
        Optional<Product> existing = products.stream()
                .filter(p -> p.sku().equals(product.sku()))
                .findFirst();

        if (existing.isPresent()) {
            // SKU déjà existant → HTTP 409 Conflict
            return Response.status(Response.Status.CONFLICT)
                    .entity("SKU already exists")
                    .build();
        }

        // Ajout du produit en mémoire
        products.add(product);

        // Retour HTTP 201 Created avec le produit ajouté
        return Response.status(Response.Status.CREATED)
                .entity(product)
                .build();
    }

    // ⚠️ Solution temporaire pour la pédagogie.
    // Dans une vraie application, la gestion des données
    // devrait être externalisée dans un repository/service.
    public static void clearProducts() {
        products.clear();
    }
}
