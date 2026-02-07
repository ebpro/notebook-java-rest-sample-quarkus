package org.acme.persistence.v1;

import jakarta.annotation.Nonnull;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

import org.acme.persistence.ProductEntity;

/**
 * Standard JPA Repository for ProductEntity.
 *
 * <p>
 * Pedagogical points:
 * <ul>
 * <li>Direct usage of the {@code EntityManager} for database interactions.</li>
 * <li>Writing explicit JPQL (Java Persistence Query Language) queries.</li>
 * <li>Understanding the persistence context and entity lifecycle (Managed,
 * Detached, Removed).</li>
 * </ul>
 */
@ApplicationScoped
public class ProductRepositoryV1 {

    /**
     * Injected by the CDI container.
     * The gateway to all JPA operations.
     */
    @PersistenceContext
    EntityManager em;

    /**
     * Persists the entity into the database.
     *
     * @param product The entity to save.
     * @return The managed persistence entity.
     */
    public ProductEntity persist(ProductEntity product) {
        em.persist(product);
        return product;
    }

    /**
     * Retrieves all products using an explicit JPQL query.
     */
    @Nonnull
    public List<ProductEntity> findAll() {
        return em.createQuery("SELECT p FROM ProductEntity p", ProductEntity.class)
                .getResultList();
    }

    /**
     * Finds a product by its unique business key.
     *
     * @param sku The SKU to search for.
     * @return The found entity or null.
     */
    public Optional<ProductEntity> findBySku(String sku) {
        return em.createQuery("SELECT p FROM ProductEntity p WHERE p.sku = :sku", ProductEntity.class)
                .setParameter("sku", sku)
                .getResultStream()
                .findFirst();
    }

    /**
     * Removes the entity from the database.
     * Focuses on ensuring the entity is managed before removal.
     */
    public void delete(ProductEntity product) {
        em.remove(em.contains(product) ? product : em.merge(product));
    }

    /**
     * Utility method for test cleanup.
     * Demonstrates bulk delete operations via JPQL.
     */
    public void deleteAll() {
        em.createQuery("DELETE FROM ProductEntity").executeUpdate();
    }

    /**
     * Synchronizes the persistence context with the underlying database.
     */
    public void flush() {
        em.flush();
    }
}
