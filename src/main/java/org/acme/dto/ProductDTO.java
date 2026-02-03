package org.acme.dto;

import java.math.BigDecimal;

/**
 * DTO utilisé pour exposer un produit via l'API.
 *
 * Points clés :
 * 1. Représente les données du produit telles qu'elles doivent être exposées.
 * 2. Ne contient aucune logique métier ni persistence.
 * 3. Immuable grâce au `record`.
 *
 * Exemple d'utilisation :
 * GET /api/v4/products ou GET /api/v4/products/{sku}
 * Réponse JSON :
 * {
 * "sku": "SKU123",
 * "name": "Chaise",
 * "price": 50.0,
 * "stock": 10
 * }
 */
public record ProductDTO(
        String sku,
        String name,
        BigDecimal price,
        int stock) {
}
