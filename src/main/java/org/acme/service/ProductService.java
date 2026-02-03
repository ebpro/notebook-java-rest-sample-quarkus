package org.acme.service;

import org.acme.persistence.ProductEntity;
import org.acme.persistence.ProductRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.util.List;

/**
 * Service métier pour Product.
 *
 * 🔹 Orchestration des opérations sur les produits.
 * 🔹 Validation des règles globales (ex : SKU unique, existence produit).
 * 🔹 Transactionnel pour les modifications (création, suppression).
 *
 * Important :
 * - Ne contient pas de logique de persistance détaillée (déjà dans le repository).
 * - Ne s’occupe pas du mapping DTO ↔ Entity (sera introduit en V4).
 */
@ApplicationScoped
public class ProductService {

    private final ProductRepository repository;

    /**
     * Injection du repository via constructeur (CDI).
     * Quarkus s’occupe de fournir l’instance.
     */
    @Inject
    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    /**
     * Récupère tous les produits.
     * @return liste de ProductEntity
     */
    public List<ProductEntity> getAll() {
        return repository.findAll();
    }

    /**
     * Récupère un produit par SKU.
     * @param sku identifiant unique
     * @return ProductEntity trouvé
     * @throws NotFoundException si le produit n'existe pas
     */
    public ProductEntity getBySku(String sku) {
        ProductEntity p = repository.findBySku(sku);
        if (p == null)
            throw new NotFoundException("Product not found");
        return p;
    }

    /**
     * Crée un produit.
     * Vérifie que le SKU est unique.
     * @param product produit à créer
     * @return ProductEntity créé
     * @throws WebApplicationException si le SKU existe déjà
     */
    @Transactional
    public ProductEntity create(ProductEntity product) {
        if (repository.findBySku(product.getSku()) != null) {
            throw new WebApplicationException(
                    "Product with this SKU already exists",
                    Response.Status.CONFLICT
            );
        }
        return repository.persist(product);
    }

    /**
     * Supprime un produit par SKU.
     * @param sku identifiant unique
     * @throws NotFoundException si le produit n'existe pas
     */
    @Transactional
    public void delete(String sku) {
        ProductEntity p = repository.findBySku(sku);
        if (p == null)
            throw new NotFoundException("Product not found");
        repository.delete(p);
    }
}
