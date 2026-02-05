package org.acme.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;
import java.util.List;

/**
 * Repository Panache pour ProductEntity (V5).
 *
 * 🔹 Objectif : simplifier le code JPA standard grâce à Panache.
 * 🔹 Permet d’écrire moins de boilerplate (pas besoin d’EntityManager
 * explicite).
 * 🔹 Toujours pas de règles métier ici, juste persistance et requêtes.
 */
@ApplicationScoped
public class ProductRepositoryV5 implements PanacheRepository<ProductEntity> {

    // -------------------------------
    // Méthodes custom “Panache style”
    // -------------------------------

    /**
     * Cherche un produit par SKU.
     *
     * @param sku SKU du produit
     * @return ProductEntity si trouvé, sinon null
     */
    public ProductEntity findBySku(String sku) {
        // find(...) est fourni par PanacheRepository
        return find("sku", sku).firstResult();
    }

    /**
     * Retourne tous les produits en stock (stock > 0).
     *
     * @return liste de ProductEntity
     */
    public List<ProductEntity> findInStock() {
        return list("stock > 0");
    }

    /**
     * Cherche les produits dont le prix est dans une plage donnée.
     *
     * @param min prix minimal
     * @param max prix maximal
     * @return liste de ProductEntity
     */
    public List<ProductEntity> findByPriceRange(BigDecimal min, BigDecimal max) {
        // ?1 et ?2 sont des paramètres positionnels
        return list("price >= ?1 and price <= ?2", min, max);
    }

}
