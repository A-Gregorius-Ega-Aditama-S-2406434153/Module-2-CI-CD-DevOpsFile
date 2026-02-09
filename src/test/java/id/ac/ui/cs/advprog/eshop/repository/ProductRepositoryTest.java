package id.ac.ui.cs.advprog.eshop.repository;

import id.ac.ui.cs.advprog.eshop.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

class ProductRepositoryTest {

    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository = new ProductRepository();
    }

    @Test
    void testCreateAndFind() {
        Product product = buildProduct(
                "eb558e9f-1c39-460e-8860-71af6af63bd6",
                "Sampo Cap Bambang",
                100
        );
        productRepository.create(product);

        Iterator<Product> productIterator = productRepository.findAll();
        assertTrue(productIterator.hasNext());
        Product savedProduct = productIterator.next();
        assertEquals(product.getProductId(), savedProduct.getProductId());
        assertEquals(product.getProductName(), savedProduct.getProductName());
        assertEquals(product.getProductQuantity(), savedProduct.getProductQuantity());
    }

    @Test
    void testFindAllIfEmpty() {
        Iterator<Product> productIterator = productRepository.findAll();
        assertFalse(productIterator.hasNext());
    }

    @Test
    void testFindAllIfMoreThanOneProduct() {
        Product product1 = buildProduct(
                "eb558e9f-1c39-460e-8860-71af6af63bd6",
                "Sampo Cap Bambang",
                100
        );
        productRepository.create(product1);

        Product product2 = buildProduct(
                "a0f9de46-90b1-437d-a0bf-d0821dde9096",
                "Sampo Cap Usep",
                50
        );
        productRepository.create(product2);

        Iterator<Product> productIterator = productRepository.findAll();
        assertTrue(productIterator.hasNext());

        Product savedProduct = productIterator.next();
        assertEquals(product1.getProductId(), savedProduct.getProductId());

        savedProduct = productIterator.next();
        assertEquals(product2.getProductId(), savedProduct.getProductId());

        assertFalse(productIterator.hasNext());
    }

    @Test
    void testFindByIdWhenFound() {
        Product product = buildProduct(
                "f1b9f202-6b20-44fd-a1a5-b9a29b3cbce2",
                "Sampo Cap Guntur",
                25
        );
        productRepository.create(product);

        Product foundProduct = productRepository.findById(product.getProductId());
        assertNotNull(foundProduct);
        assertEquals(product.getProductId(), foundProduct.getProductId());
    }

    @Test
    void testFindByIdWhenMissing() {
        Product foundProduct = productRepository.findById("missing-id");
        assertNull(foundProduct);
    }

    @Test
    void testFindByIdIgnoresNullProductId() {
        Product product = buildProduct(null, "Sampo Cap Kucing", 10);
        productRepository.create(product);

        Product foundProduct = productRepository.findById("some-id");
        assertNull(foundProduct);
    }

    @Test
    void testUpdateWhenFound() {
        Product product = buildProduct(
                "2d4a1841-97d7-4104-8ab3-4c90f4f7f9c9",
                "Sampo Cap Ayam",
                12
        );
        productRepository.create(product);

        Product updatedProduct = buildProduct(
                product.getProductId(),
                "Sampo Cap Kuda",
                99
        );

        Product result = productRepository.update(updatedProduct);
        assertNotNull(result);
        assertEquals("Sampo Cap Kuda", result.getProductName());
        assertEquals(99, result.getProductQuantity());
    }

    @Test
    void testUpdateWhenNullProduct() {
        Product result = productRepository.update(null);
        assertNull(result);
    }

    @Test
    void testUpdateWhenNullProductId() {
        Product updatedProduct = buildProduct(null, "Sampo Cap Lumba", 20);
        Product result = productRepository.update(updatedProduct);
        assertNull(result);
    }

    @Test
    void testUpdateWhenMissing() {
        Product updatedProduct = buildProduct(
                "missing-id",
                "Sampo Cap Harimau",
                31
        );

        Product result = productRepository.update(updatedProduct);
        assertNull(result);
    }

    @Test
    void testDeleteByIdWhenFound() {
        Product product = buildProduct(
                "7ee058b6-5172-4e6f-b659-b7a10b5b3f46",
                "Sampo Cap Hiu",
                45
        );
        productRepository.create(product);

        boolean deleted = productRepository.deleteById(product.getProductId());
        assertTrue(deleted);
        assertNull(productRepository.findById(product.getProductId()));
    }

    @Test
    void testDeleteByIdWhenMissing() {
        boolean deleted = productRepository.deleteById("missing-id");
        assertFalse(deleted);
    }

    @Test
    void testDeleteByIdWhenNull() {
        boolean deleted = productRepository.deleteById(null);
        assertFalse(deleted);
    }

    private Product buildProduct(String id, String name, int quantity) {
        Product product = new Product();
        product.setProductId(id);
        product.setProductName(name);
        product.setProductQuantity(quantity);
        return product;
    }
}

