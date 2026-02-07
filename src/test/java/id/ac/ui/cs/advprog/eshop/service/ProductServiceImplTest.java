package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    ProductServiceImpl productService;

    @Test
    void testCreateAssignsIdWhenNull() {
        Product product = new Product();
        when(productRepository.create(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Product createdProduct = productService.create(product);

        assertNotNull(createdProduct.getProductId());
        assertFalse(createdProduct.getProductId().isBlank());
        verify(productRepository).create(product);
    }

    @Test
    void testCreateAssignsIdWhenBlank() {
        Product product = new Product();
        product.setProductId("   ");
        when(productRepository.create(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Product createdProduct = productService.create(product);

        assertNotNull(createdProduct.getProductId());
        assertFalse(createdProduct.getProductId().isBlank());
        verify(productRepository).create(product);
    }

    @Test
    void testCreateKeepsExistingId() {
        Product product = new Product();
        product.setProductId("existing-id");
        when(productRepository.create(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Product createdProduct = productService.create(product);

        assertEquals("existing-id", createdProduct.getProductId());
        verify(productRepository).create(product);
    }

    @Test
    void testFindAllReturnsList() {
        Product product1 = buildProduct("id-1", "Sampo A", 10);
        Product product2 = buildProduct("id-2", "Sampo B", 20);
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2).iterator());

        List<Product> products = productService.findAll();

        assertEquals(2, products.size());
        assertEquals("id-1", products.get(0).getProductId());
        assertEquals("id-2", products.get(1).getProductId());
        verify(productRepository).findAll();
    }

    @Test
    void testFindByIdReturnsProduct() {
        Product product = buildProduct("id-3", "Sampo C", 5);
        when(productRepository.findById("id-3")).thenReturn(product);

        Product foundProduct = productService.findById("id-3");

        assertNotNull(foundProduct);
        assertEquals("id-3", foundProduct.getProductId());
        verify(productRepository).findById("id-3");
    }

    @Test
    void testUpdateReturnsUpdatedProduct() {
        Product product = buildProduct("id-4", "Sampo D", 7);
        when(productRepository.update(product)).thenReturn(product);

        Product updatedProduct = productService.update(product);

        assertNotNull(updatedProduct);
        assertEquals("id-4", updatedProduct.getProductId());
        verify(productRepository).update(product);
    }

    @Test
    void testDeleteByIdReturnsTrue() {
        when(productRepository.deleteById("id-5")).thenReturn(true);

        boolean deleted = productService.deleteById("id-5");

        assertTrue(deleted);
        verify(productRepository).deleteById("id-5");
    }

    @Test
    void testDeleteByIdReturnsFalse() {
        when(productRepository.deleteById("id-6")).thenReturn(false);

        boolean deleted = productService.deleteById("id-6");

        assertFalse(deleted);
        verify(productRepository).deleteById("id-6");
    }

    private Product buildProduct(String id, String name, int quantity) {
        Product product = new Product();
        product.setProductId(id);
        product.setProductName(name);
        product.setProductQuantity(quantity);
        return product;
    }
}
