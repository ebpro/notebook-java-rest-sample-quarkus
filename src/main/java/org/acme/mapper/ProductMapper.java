package org.acme.mapper;

import org.acme.persistence.ProductEntity;
import org.acme.dto.ProductDTO;
import org.acme.dto.CreateProductRequest;
import org.acme.domain.Product;

public class ProductMapper {

    private ProductMapper() {
    }

    /**
     * Conversion pour la sortie (API)
     */
    public static ProductDTO toDto(ProductEntity entity) {
        return new ProductDTO(
                entity.getSku(),
                entity.getName(),
                entity.getPrice(),
                entity.getStock());
    }

    /**
     * Conversion pour l'entrée (Persistance) avec validation Domaine
     */
    public static ProductEntity toEntity(CreateProductRequest request) {
        // Étape 1 : Création de l'objet métier (Validation des règles de gestion)
        // C'est ici que l' IllegalArgumentException est jetée si prix < 0.
        var domain = Product.of(
                request.sku(),
                request.name(),
                request.price(),
                request.stock());

        // Étape 2 : Si on arrive ici, le domaine est valide, on peut créer l'entité
        return new ProductEntity(
                domain.sku(),
                domain.name(),
                domain.price(),
                domain.stock());
    }
}
