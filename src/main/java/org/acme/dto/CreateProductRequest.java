package org.acme.dto;

import java.math.BigDecimal;

/**
 * DTO utilisé pour créer un produit via l'API.
 *
 * Points clés :
 * 1. Représente uniquement les données nécessaires pour la création.
 * 2. Ne contient aucune logique métier ni persistence.
 * 3. Immuable grâce au `record`.
 *
 * Exemple d'utilisation dans une ressource REST :
 * POST /api/v4/products
 * Body JSON :
 * {
 * "sku": "SKU123",
 * "name": "Chaise",
 * "price": 50.0,
 * "stock": 10
 * }
 */
public record CreateProductRequest(
                String sku,
                String name,
                BigDecimal price,
                int stock) {
}
