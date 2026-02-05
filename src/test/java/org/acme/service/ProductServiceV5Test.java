package org.acme.service;

import org.acme.persistence.ProductRepositoryV5;
import org.acme.persistence.ProductEntity;
import org.acme.dto.CreateProductRequest;
import org.acme.dto.ProductDTO;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.List;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;

import static org.mockito.Mockito.*;

@DisplayName("ProductServiceV5 Unit Tests")
@Disabled
class ProductServiceV5Test {

    @Mock
    ProductRepositoryV5 repository;

    ProductServiceV5 service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        service = new ProductServiceV5(repository);
    }

    @Test
    void getAll_returnsMappedDTOs() {
        ProductEntity e = new ProductEntity("SKU1", "Chair", new BigDecimal("12.34"), 5);
        when(repository.listAll()).thenReturn(List.of(e));

        var result = service.getAll();

        Assertions.assertEquals(1, result.size());
        ProductDTO dto = result.get(0);
        Assertions.assertEquals("SKU1", dto.sku());
        Assertions.assertEquals("Chair", dto.name());
        Assertions.assertEquals(new BigDecimal("12.34"), dto.price());
        Assertions.assertEquals(5, dto.stock());
    }

    @Test
    void getBySku_found() {
        ProductEntity e = new ProductEntity("SKU2", "Table", new BigDecimal("99.99"), 2);
        when(repository.findBySku("SKU2")).thenReturn(e);

        ProductDTO dto = service.getBySku("SKU2");

        Assertions.assertEquals("SKU2", dto.sku());
        Assertions.assertEquals("Table", dto.name());
    }

    @Test
    void getBySku_notFound_throwsNotFound() {
        when(repository.findBySku("MISSING")).thenReturn(null);

        Assertions.assertThrows(NotFoundException.class, () -> service.getBySku("MISSING"));
    }

    @Test
    void create_conflict_throwsWebApplicationException() {
        CreateProductRequest req = new CreateProductRequest("SKU3", "Lamp", new BigDecimal("5.00"), 10);
        when(repository.findBySku("SKU3")).thenReturn(new ProductEntity());

        WebApplicationException ex = Assertions.assertThrows(WebApplicationException.class, () -> service.create(req));
        Assertions.assertEquals(409, ex.getResponse().getStatus());
    }

    @Test
    void create_success_persistsAndReturnsDTO() {
        CreateProductRequest req = new CreateProductRequest("SKU4", "Sofa", new BigDecimal("199.99"), 1);
        when(repository.findBySku("SKU4")).thenReturn(null);

        // repository.persist(entity) is void — ensure no exception and returned DTO
        // matches
        ProductDTO dto = service.create(req);

        Assertions.assertEquals("SKU4", dto.sku());
        Assertions.assertEquals("Sofa", dto.name());
        Assertions.assertEquals(new BigDecimal("199.99"), dto.price());
        Assertions.assertEquals(1, dto.stock());
        verify(repository).persist(Mockito.any(ProductEntity.class));
    }

    @Test
    void delete_notFound_throwsNotFound() {
        when(repository.findBySku("NOT")).thenReturn(null);

        Assertions.assertThrows(NotFoundException.class, () -> service.delete("NOT"));
    }

    @Test
    void delete_found_deletesEntity() {
        ProductEntity e = new ProductEntity("SKU5", "Desk", new BigDecimal("49.99"), 3);
        when(repository.findBySku("SKU5")).thenReturn(e);

        service.delete("SKU5");

        verify(repository).delete(e);
    }
}
