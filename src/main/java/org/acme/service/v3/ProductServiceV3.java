package org.acme.service.v3;

import org.acme.persistence.ProductEntity;
import org.acme.persistence.v1.ProductRepositoryV1;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

/**
 * Business Service V3 demonstrating basic Service Layer responsibilities.
 *
 * <p>
 * Learning Objectives:
 * <ul>
 * <li>Understanding Dependency Injection (CDI) of Repositories.</li>
 * <li>Managing Transactional boundaries using {@code @Transactional}.</li>
 * <li>Implementing basic business rules (e.g., SKU uniqueness) before
 * persistence.</li>
 * <li>Handling exceptions using JAX-RS standard web exceptions.</li>
 * </ul>
 */
@ApplicationScoped
public class ProductServiceV3 {

    private final ProductRepositoryV1 repository;

    /**
     * Constructor injection of the repository.
     * CDI provides the instance at runtime, facilitating unit testing.
     */
    @Inject
    public ProductServiceV3(ProductRepositoryV1 repository) {
        this.repository = repository;
    }

    /**
     * Fetches all products directly from the persistence context.
     * * @return A list of managed database entities.
     */
    public List<ProductEntity> getAll() {
        return repository.findAll();
    }

    /**
     * Retrieves a single product by SKU.
     * Uses the Repository's Optional return type to safely throw a 404 exception.
     *
     * @param sku The business identifier.
     * @return The found Entity.
     * @throws NotFoundException (404) if the SKU is missing.
     */
    public ProductEntity getBySku(String sku) {
        return repository.findBySku(sku)
                .orElseThrow(() -> new NotFoundException(
                        Response.status(Response.Status.NOT_FOUND)
                                .entity("Product not found: " + sku)
                                .type(MediaType.TEXT_PLAIN_TYPE)
                                .build()));
    }

    /**
     * Orchestrates product creation logic.
     * *
     * <p>
     * Pedagogical Point: We check for existence before persisting to avoid
     * low-level SQL constraint violations, providing a cleaner API error.
     *
     * @param product The entity to persist.
     * @return The persisted entity with its generated technical ID.
     * @throws WebApplicationException (409) if the SKU is already taken.
     */
    @Transactional
    public ProductEntity create(ProductEntity product) {
        // Business Rule: SKU must be unique.
        // Since Repository returns Optional, we check presence.
        if (repository.findBySku(product.getSku()).isPresent()) {
            throw new WebApplicationException(
                    Response.status(Response.Status.CONFLICT)
                            .entity("Product SKU already exists: " + product.getSku())
                            .type(MediaType.TEXT_PLAIN_TYPE)
                            .build());
        }
        return repository.persist(product);
    }

    /**
     * Deletes a product by SKU.
     * Ensure the transaction is active to allow the EntityManager to remove the
     * entity.
     */
    @Transactional
    public void delete(String sku) {
        ProductEntity entity = getBySku(sku); // Reuse retrieval logic to handle 404
        repository.delete(entity);
    }

    /**
     * Bulk delete for environment cleanup.
     * ONLY for testing purposes, not recommended for production use due to
     * potential performance issues and lack of safety checks.
     *
     */
    @Transactional
    public void clearAll() {
        repository.deleteAll();
    }
}
