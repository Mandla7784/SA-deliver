import main.java.Product;
import main.java.ProductService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProductServiceTest {


    @Test
    public void testGetAllProducts() {
        ProductService productService = new ProductService();
        productService.addProduct(new Product(10) , "1");
        productService.addProduct(new Product(20) , "2");
        List<Product> products = productService.getAllProducts();
        assertEquals(2, products.size());

    }
    @Test
    public  void testGetProductById() {
        ProductService productService = new ProductService();

    }
}
