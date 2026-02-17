package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    ProductService productService;

    ProductController productController;

    @BeforeEach
    void setUp() {
        productController = new ProductController(productService);
    }

    @Test
    void testCreateProductPage() {
        Model model = new ConcurrentModel();

        String viewName = productController.createProductPage(model);

        assertEquals("CreateProduct", viewName);
        assertTrue(model.containsAttribute("product"));
        assertNotNull(model.getAttribute("product"));
    }

    @Test
    void testCreateProductPost() {
        Product product = new Product();
        Model model = new ConcurrentModel();

        String viewName = productController.createProductPost(product, model);

        verify(productService).create(product);
        assertEquals("redirect:list", viewName);
    }

    @Test
    void testProductListPage() {
        Product product = buildProduct("id-1", "Sampo A", 3);
        List<Product> products = Collections.singletonList(product);
        when(productService.findAll()).thenReturn(products);
        Model model = new ConcurrentModel();

        String viewName = productController.productListPage(model);

        assertEquals("ProductList", viewName);
        assertEquals(products, model.getAttribute("products"));
        verify(productService).findAll();
    }

    @Test
    void testEditProductPageWhenFound() {
        Product product = buildProduct("id-2", "Sampo B", 9);
        when(productService.findById("id-2")).thenReturn(product);
        Model model = new ConcurrentModel();

        String viewName = productController.editProductPage("id-2", model);

        assertEquals("EditProduct", viewName);
        assertEquals(product, model.getAttribute("product"));
        verify(productService).findById("id-2");
    }

    @Test
    void testEditProductPageWhenMissing() {
        when(productService.findById("missing-id")).thenReturn(null);
        Model model = new ConcurrentModel();

        String viewName = productController.editProductPage("missing-id", model);

        assertEquals("redirect:/product/list", viewName);
        assertFalse(model.containsAttribute("product"));
        verify(productService).findById("missing-id");
    }

    @Test
    void testEditProductPost() {
        Product product = buildProduct("id-3", "Sampo C", 12);

        String viewName = productController.editProductPost(product);

        verify(productService).update(product);
        assertEquals("redirect:/product/list", viewName);
    }

    @Test
    void testDeleteProductPost() {
        String viewName = productController.deleteProductPost("id-4");

        verify(productService).deleteById("id-4");
        assertEquals("redirect:/product/list", viewName);
    }

    private Product buildProduct(String id, String name, int quantity) {
        Product product = new Product();
        product.setProductId(id);
        product.setProductName(name);
        product.setProductQuantity(quantity);
        return product;
    }
}
