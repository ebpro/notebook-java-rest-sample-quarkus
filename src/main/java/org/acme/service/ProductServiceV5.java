package org.acme.service;

import org.acme.dto.CreateProductRequest;
import org.acme.dto.ProductDTO;
import org.acme.persistence.ProductEntity;
import org.acme.persistence.ProductRepositoryV5;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.util.List;

/**
 * Service métier V5 pour Product
 *
 * 🔹 Orchestration des opérations sur les produits.
 * 🔹 Utilise ProductRepositoryV5 avec Panache.
 * 🔹 Mappe ProductEntity ↔ ProductDTO pour la Resource V5.
 *
 * Remarques pédagogiques :
 * - Panache simplifie les requêtes et l’accès à la base.
 * - DTO permet de séparer l’API des détails de persistance.
 * - @Transactional est nécessaire pour les opérations qui modifient la base.
 * - Les exceptions JAX-RS (NotFoundException, WebApplicationException)
 * permettent de renvoyer directement des codes HTTP pertinents.
 */
@ApplicationScoped
public class ProductServiceV5 {

    private final ProductRepositoryV5 repository;

    /**
     * Injection du repository via constructeur
     */
    public ProductServiceV5(ProductRepositoryV5 repository) {
        this.repository = repository;
    }

    /**
     * Récupère tous les produits
     * 
     * @return liste de ProductDTO
     */
    public List<ProductDTO> getAll() {
        return repository.listAll().stream() // listAll() de Panache
                .map(this::toDTO)
                .toList();
    }

    /**
     * Récupère un produit par SKU
     * 
     * @param sku identifiant unique
     * @return ProductDTO correspondant
     * @throws NotFoundException si le produit n'existe pas
     */
    public ProductDTO getBySku(String sku) {
        ProductEntity p = repository.findBySku(sku);
        if (p == null) {
            throw new NotFoundException("Product not found");
        }
        return toDTO(p);
    }

    /**
     * Crée un nouveau produit
     * 
     * @param request données du produit
     * @return ProductDTO créé
     * @throws WebApplicationException si SKU déjà existant
     */
    @Transactional
    public ProductDTO create(CreateProductRequest request) {
        if (repository.findBySku(request.sku()) != null) {
            throw new WebApplicationException(
                    "Product with this SKU already exists",
                    Response.Status.CONFLICT);
        }

        ProductEntity entity = new ProductEntity(
                request.sku(),
                request.name(),
                request.price(),
                request.stock());

        repository.persist(entity); // Panache gère l’EntityManager automatiquement
        return toDTO(entity);
    }

    /**
     * Supprime un produit par SKU
     * 
     * @param sku identifiant unique
     * @throws NotFoundException si le produit n'existe pas
     */
    @Transactional
    public void delete(String sku) {
        ProductEntity p = repository.findBySku(sku);
        if (p == null) {
            throw new NotFoundException("Product not found");
        }
        repository.delete(p); // Panache supprime l'entité
    }

    /**
     * Mapping ProductEntity → ProductDTO
     * 
     * @param entity entité JPA
     * @return DTO correspondant
     */
    private ProductDTO toDTO(ProductEntity entity) {
        return new ProductDTO(
                entity.getSku(),
                entity.getName(),
                entity.getPrice(),
                entity.getStock());
    }
}
