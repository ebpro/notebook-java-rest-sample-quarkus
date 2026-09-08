package org.acme.service.v4;

import org.acme.dto.CreateProductRequest;
import org.acme.dto.ProductDTO;
import org.acme.mapper.ProductMapper;
import org.acme.persistence.ProductEntity;
import org.acme.persistence.v1.ProductRepositoryV1;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Business Service V4 demonstrating the Encapsulation pattern.
 *
 * <p>
 * Learning Objectives:
 * <ul>
 * <li>Decoupling the API from Persistence by mapping Entities to DTOs.</li>
 * <li>Implementing "Input DTOs" (Request) and "Output DTOs" (Response).</li>
 * <li>Centralizing mapping logic in specialized components (Mappers).</li>
 * </ul>
 */
@ApplicationScoped
public class ProductServiceV4 {

    private final ProductRepositoryV1 repository;

    public ProductServiceV4(ProductRepositoryV1 repository) {
        this.repository = repository;
    }

    /**
     * Retrieves all products and transforms them into DTOs.
     * Prevents internal ID leakage.
     */
    public List<ProductDTO> getAll() {
        return repository.findAll().stream()
                .map(ProductMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a DTO by SKU.
     * Demonstrates functional transformation of an Optional Entity.
     */
    public ProductDTO getBySku(String sku) {
        return repository.findBySku(sku)
                .map(ProductMapper::toDto)
                .orElseThrow(() -> new NotFoundException(
                        Response.status(Response.Status.NOT_FOUND)
                                .entity("Product not found: " + sku)
                                .type(MediaType.TEXT_PLAIN_TYPE)
                                .build()));
    }

    /**
     * Creates a product from a request DTO.
     * *
     * <p>
     * Pedagogical Point: The Service orchestrates three steps:
     * 1. Mapping/Validating the input.
     * 2. Checking business constraints (Unique SKU).
     * 3. Returning a sanitized view of the result.
     */
    @Transactional
    public ProductDTO create(CreateProductRequest request) {
        // Step 1: Convert DTO to Entity via Mapper
        ProductEntity entity = ProductMapper.toEntity(request);

        // Step 2: Check persistence integrity
        if (repository.findBySku(entity.getSku()).isPresent()) {
            throw new WebApplicationException(
                    Response.status(Response.Status.CONFLICT)
                            .entity("Product SKU already exists: " + entity.getSku())
                            .type(MediaType.TEXT_PLAIN_TYPE)
                            .build());
        }

        // Step 3: Persist and Return DTO
        repository.persist(entity);
        return ProductMapper.toDto(entity);
    }

    /**
     * Deletes by SKU using a functional "find and act" approach.
     */
    @Transactional
    public void delete(String sku) {
        repository.findBySku(sku)
                .ifPresentOrElse(
                        repository::delete,
                        () -> {
                            throw new NotFoundException(
                                    Response.status(Response.Status.NOT_FOUND)
                                            .entity("Product not found: " + sku)
                                            .type(MediaType.TEXT_PLAIN_TYPE)
                                            .build());
                        });
    }

    /**
     * Utility method to clear all products (for testing purposes).
     * ONLY for testing purposes, not recommended for production use due to
     * potential performance issues and lack of safety checks.
     */
    @Transactional
    public void clearAll() {
        repository.deleteAll();
    }
}
