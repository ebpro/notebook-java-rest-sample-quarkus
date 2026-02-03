package org.acme.persistence;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Entité JPA représentant un produit dans la base de données.
 *
 * 🔹 Objectif : uniquement gérer la persistance.
 * 🔹 Ne contient pas de logique métier complexe.
 */
@Entity
@Table(name = "products") // Nom de la table dans la base
public class ProductEntity {

    /**
     * Identifiant auto-généré.
     * Utilisé comme clé primaire.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * SKU unique et obligatoire.
     * Sert à identifier un produit de manière métier.
     */
    @Column(unique = true, nullable = false)
    private String sku;

    /**
     * Nom du produit (obligatoire)
     */
    @Column(nullable = false)
    private String name;

    /**
     * Prix du produit (obligatoire)
     */
    @Column(nullable = false)
    private BigDecimal price;

    /**
     * Stock disponible (obligatoire)
     */
    @Column(nullable = false)
    private int stock;

    // -------------------------------
    // Constructeurs
    // -------------------------------

    /**
     * Constructeur par défaut requis par JPA.
     */
    public ProductEntity() {
    }

    /**
     * Constructeur pratique pour créer rapidement une entité.
     */
    public ProductEntity(String sku, String name, BigDecimal price, int stock) {
        this.sku = sku;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    // -------------------------------
    // Getters / Setters
    // -------------------------------

    public Long getId() {
        return id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
