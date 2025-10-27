import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import main.java.Product;
import main.java.ProductService;

public class ProductServiceTest {


    @Test
    public void testGetAllProducts() {
        ProductService productService = new ProductService();
        Product product1 = new Product("Test Product 1", "Description 1", 100.0, 10, "Category1");
        Product product2 = new Product("Test Product 2", "Description 2", 200.0, 20, "Category2");
        productService.addProduct(product1);
        productService.addProduct(product2);
        List<Product> products = productService.getAllProducts();
        assertEquals(6, products.size()); // 4 sample products + 2 test products
    }
    @Test
    public  void testGetProductById() {
        ProductService productService = new ProductService();
        Product testProduct = new Product("Test Product", "Test Description", 100.0, 10, "TestCategory");
        productService.addProduct(testProduct);
        Product product = productService.getProductById(testProduct.getId());
        assertEquals(10, product.getStock());
    }

    @Test
    public void testUpdateProductStocks() {
        ProductService productService = new ProductService();
        Product testProduct = new Product("Test Product", "Test Description", 100.0, 10, "TestCategory");
        productService.addProduct(testProduct);
        boolean result = productService.updateProductStock(testProduct.getId(), 5);
        assertTrue(result);
        Product product = productService.getProductById(testProduct.getId());
        assertEquals(5, product.getStock());
    }

    @Test
    public void testDeleteProduct() {
        ProductService productService = new ProductService();
        Product testProduct = new Product("Test Product", "Test Description", 100.0, 10, "TestCategory");
        productService.addProduct(testProduct);
        boolean result = productService.deleteProduct(testProduct.getId());
        assertTrue(result);
        assertEquals(4, productService.getAllProducts().size()); // Only sample products remain
    }

    @Test
    public void testAddProduct() {
        ProductService productService = new ProductService();
        Product testProduct = new Product("Test Product", "Test Description", 100.0, 10, "TestCategory");
        Product addedProduct = productService.addProduct(testProduct);
        assertNotNull(addedProduct);
        assertEquals(5, productService.getAllProducts().size()); // 4 sample + 1 test
    }
}


