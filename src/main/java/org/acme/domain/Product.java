package org.acme.domain;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Product métier immuable avec validation renforcée.
 */
public record Product(String sku, String name, BigDecimal price, int stock) {

    /**
     * CONSTRUCTEUR COMPACT
     * Sécurité absolue : Toute création d'instance passe par ici,
     * y compris le constructeur canonique généré par Java.
     */
    public Product {
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
        // Pas besoin d'assigner this.sku = sku; Java le fait automatiquement après ce
        // bloc.
    }

    /**
     * Factory method (conservée pour l'expressivité du code).
     */
    public static Product of(String sku, String name, BigDecimal price, int stock) {
        return new Product(sku, name, price, stock);
    }

    /**
     * Création d'une nouvelle instance avec un nom différent.
     */
    public Product withName(String newName) {
        return new Product(this.sku, newName, this.price, this.stock);
    }

    /**
     * Création d'une nouvelle instance avec un prix différent.
     */
    public Product withPrice(BigDecimal newPrice) {
        return new Product(this.sku, this.name, newPrice, this.stock);
    }

    /**
     * Création d'une nouvelle instance avec un stock différent.
     */
    public Product withStock(int newStock) {
        return new Product(this.sku, this.name, this.price, newStock);
    }

    public boolean isInStock() {
        return stock > 0;
    }

    @Override
    public String toString() {
        return String.format("Product[sku=%s, name=%s, price=%s, stock=%d]",
                sku, name, price, stock);
    }
}
