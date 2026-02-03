package org.acme.mapper;

import org.acme.persistence.ProductEntity;
import org.acme.dto.ProductDTO;
import org.acme.dto.CreateProductRequest;

/**
 * Mapper pour convertir entre :
 * - ProductEntity (persistence)
 * - ProductDTO / CreateProductRequest (API)
 *
 * Séparation claire des responsabilités :
 * - ProductEntity : gère la base de données
 * - ProductDTO / CreateProductRequest : gère l’API / l’exposition
 * - ProductMapper : fait la traduction entre les deux
 *
 * 🔹 Toutes les méthodes sont statiques → pas besoin d’instance.
 */
public class ProductMapper {

    // Constructeur privé pour empêcher l'instanciation
    private ProductMapper() {
    }

    /**
     * Transforme une entité JPA en DTO pour l’API.
     *
     * @param entity l'entité ProductEntity
     * @return ProductDTO correspondant
     */
    public static ProductDTO toDto(ProductEntity entity) {
        return new ProductDTO(
                entity.getSku(),
                entity.getName(),
                entity.getPrice(),
                entity.getStock());
    }

    /**
     * Transforme un objet reçu via l'API en entité JPA.
     *
     * @param request l'objet CreateProductRequest reçu
     * @return ProductEntity prêt à être persisté
     */
    public static ProductEntity toEntity(CreateProductRequest request) {
        return new ProductEntity(
                request.sku(),
                request.name(),
                request.price(),
                request.stock());
    }
}
