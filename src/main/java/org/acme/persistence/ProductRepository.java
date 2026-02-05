package org.acme.persistence;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

/**
 * Repository JPA pour ProductEntity.
 *
 * 🔹 Objectif : gérer uniquement la persistance (CRUD).
 * 🔹 Ne contient pas de logique métier complexe.
 * 🔹 C’est Quarkus qui injecte l’EntityManager via @PersistenceContext.
 */
@ApplicationScoped
public class ProductRepository {

    /**
     * EntityManager injecté par CDI / Quarkus.
     * Sert à interagir avec la base de données.
     */
    @PersistenceContext
    EntityManager em;

    // -------------------------------
    // Méthodes CRUD
    // -------------------------------

    /**
     * Sauvegarde un produit en base.
     *
     * @param product ProductEntity à persister
     * @return ProductEntity persistant (même instance)
     */
    public ProductEntity persist(ProductEntity product) {
        em.persist(product);
        return product;
    }

    /**
     * Retourne tous les produits.
     *
     * @return liste de ProductEntity
     */
    public List<ProductEntity> findAll() {
        return em.createQuery("SELECT p FROM ProductEntity p", ProductEntity.class)
                .getResultList();
    }

    /**
     * Cherche un produit par SKU.
     *
     * @param sku SKU du produit
     * @return ProductEntity si trouvé, sinon null
     */
    public ProductEntity findBySku(String sku) {
        return em.createQuery("SELECT p FROM ProductEntity p WHERE p.sku = :sku", ProductEntity.class)
                .setParameter("sku", sku)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

    /**
     * Supprime un produit.
     *
     * @param product ProductEntity à supprimer
     */
    public void delete(ProductEntity product) {
        // Si l’EntityManager ne connaît pas l’entité, on merge avant remove
        em.remove(em.contains(product) ? product : em.merge(product));
    }

    /**
     * Supprime tous les produits de la base.
     * ⚠️ À utiliser uniquement pour les tests, pas dans une application réelle.
     *
     */
    public void deleteAll() {
        em.createQuery("DELETE FROM ProductEntity").executeUpdate();
    }

    /**
     * Force l’exécution de toutes les opérations en attente sur la base.
     * Utile pour s’assurer que les données sont effectivement supprimées avant de
     * continuer.
     */
    public void flush() {
        em.flush();
    }

}
