package org.acme.service;

import org.acme.dto.CreateProductRequest;
import org.acme.dto.ProductDTO;
import org.acme.mapper.ProductMapper;
import org.acme.persistence.ProductEntity;
import org.acme.persistence.ProductRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service métier V4 pour Product
 *
 * 🔹 Orchestration des opérations sur les produits.
 * 🔹 Utilise ProductRepository pour la persistance.
 * 🔹 Mappe ProductEntity ↔ ProductDTO pour la Resource V4.
 *
 * Remarques pédagogiques :
 * - L’usage de DTO permet de séparer l’API des détails de persistance.
 * - @Transactional est nécessaire pour les opérations qui modifient la base.
 * - Les exceptions JAX-RS (NotFoundException, WebApplicationException)
 * permettent de renvoyer directement des codes HTTP pertinents.
 */
@ApplicationScoped
public class ProductServiceV4 {

    private final ProductRepository repository;

    /**
     * Injection du repository via constructeur
     */
    public ProductServiceV4(ProductRepository repository) {
        this.repository = repository;
    }

    /**
     * Récupère tous les produits
     *
     * @return liste de ProductDTO
     */
    public List<ProductDTO> getAll() {
        return repository.findAll().stream()
                .map(this::toDTO) // mapping Entity → DTO
                .collect(Collectors.toList());
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
        // 1. UTILISATION DU DOMAINE (via le Mapper)
        // C'est ici que l'étudiant comprend l'intérêt du Domaine :
        // Si request.price() est -10.0, Product.of() explose ici.
        ProductEntity entity = ProductMapper.toEntity(request);

        // 2. LOGIQUE TECHNIQUE (Persistance)
        if (repository.findBySku(entity.getSku()) != null) {
            throw new WebApplicationException(
                    "Product with this SKU already exists",
                    Response.Status.CONFLICT);
        }

        // 3. PERSISTANCE
        repository.persist(entity);

        // 4. RETOUR DTO
        return ProductMapper.toDto(entity);
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
        repository.delete(p);
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

    /**
     * Supprime tous les produits de la base.
     * ⚠️ À utiliser uniquement pour les tests, pas dans une application réelle.
     *
     */
    @Transactional
    public void clearAll() {
        repository.deleteAll();
    }
}
