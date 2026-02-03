package org.acme.domain;

import java.math.BigDecimal;

/**
 * Product métier immuable avec validation et méthodes fonctionnelles.
 *
 * Points clés :
 * 1. Immutabilité : tous les champs sont finals, c'est un `record`.
 * 2. Validation centralisée : factory `of()` pour vérifier les règles métier.
 * 3. Méthodes "withX" pour créer de nouvelles instances modifiées sans altérer
 * l'existant.
 * 4. Méthodes métier (ex : isInStock) pour encapsuler la logique propre au
 * produit.
 *
 * Exemple d'utilisation :
 * Product p = Product.of("SKU123", "Chaise", BigDecimal.valueOf(50), 10);
 * Product updated = p.withPrice(BigDecimal.valueOf(55));
 */
public record Product(String sku, String name, BigDecimal price, int stock) {

    /**
     * Factory method pour créer un produit avec validation métier.
     */
    public static Product of(String sku, String name, BigDecimal price, int stock) {
        if (sku == null || sku.isBlank()) {
            throw new IllegalArgumentException("SKU cannot be null or empty");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price cannot be null or negative");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }
        return new Product(sku, name, price, stock);
    }

    /**
     * Création d'une nouvelle instance avec un nom différent (immutabilité).
     */
    public Product withName(String newName) {
        return Product.of(this.sku, newName, this.price, this.stock);
    }

    /**
     * Création d'une nouvelle instance avec un prix différent.
     */
    public Product withPrice(BigDecimal newPrice) {
        return Product.of(this.sku, this.name, newPrice, this.stock);
    }

    /**
     * Création d'une nouvelle instance avec un stock différent.
     */
    public Product withStock(int newStock) {
        return Product.of(this.sku, this.name, this.price, newStock);
    }

    /**
     * Méthode métier simple pour vérifier la disponibilité.
     */
    public boolean isInStock() {
        return stock > 0;
    }

    /**
     * Pour affichage lisible.
     */
    @Override
    public String toString() {
        return String.format("Product[sku=%s, name=%s, price=%s, stock=%d]",
                sku, name, price, stock);
    }
}
