package org.acme.service;

import org.acme.dto.CreateProductRequest;
import org.acme.dto.ProductDTO;
import org.acme.mapper.ProductMapper;
import org.acme.persistence.ProductEntity;
import org.acme.persistence.ProductRepositoryV5;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException; // Standard Java

@ApplicationScoped
public class ProductServiceV5 {

    private final ProductRepositoryV5 repository;

    public ProductServiceV5(ProductRepositoryV5 repository) {
        this.repository = repository;
    }

    public List<ProductDTO> getAll() {
        return repository.listAll().stream()
                .map(ProductMapper::toDto)
                .toList();
    }

    public ProductDTO getBySku(String sku) {
        ProductEntity p = repository.findBySku(sku);
        if (p == null) {
            // On jette une exception Java standard
            throw new NoSuchElementException("Product not found");
        }
        return ProductMapper.toDto(p);
    }

    @Transactional
    public ProductDTO create(CreateProductRequest request) {
        // 1. Validation métier (jette IllegalArgumentException si invalide)
        ProductEntity entity = ProductMapper.toEntity(request);

        // 2. Règle d'intégrité (SKU unique)
        if (repository.findBySku(entity.getSku()) != null) {
            // On jette une exception d'état illégal (Conflict)
            throw new IllegalStateException("Product with this SKU already exists");
        }

        // 3. Persistance
        repository.persist(entity);

        return ProductMapper.toDto(entity);
    }

    @Transactional
    public void delete(String sku) {
        ProductEntity p = repository.findBySku(sku);
        if (p == null) {
            throw new NoSuchElementException("Product not found");
        }
        repository.delete(p);
    }

    @Transactional
    public void clearAll() {
        repository.deleteAll();
    }
}
