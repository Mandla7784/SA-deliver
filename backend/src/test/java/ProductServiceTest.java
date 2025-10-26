import main.java.Product;
import main.java.ProductService;
import org.junit.jupiter.api.BeforeEach;
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
        productService.addProduct(new Product(10) , "1");
        Product product = productService.getProductById("1");
        assertEquals(10, product.getStock());




    }

    @Test
    public void testUpdateProductStocks() {
        ProductService productService = new ProductService();
        productService.addProduct(new Product(10) , "1");
        productService.updateProductStocks("1", 5);
        Product product = productService.getProductById("1");
        assertEquals(5, product.getStock());

    }

    @Test
    public void testDeleteProduct() {
        ProductService productService = new ProductService();
        productService.addProduct(new Product(10) , "1");
        productService.deleteProduct("1");
        assertEquals(0, productService.getAllProducts().size());

    }

    @Test
    public void testAddProduct() {
        ProductService productService = new ProductService();
        productService.addProduct(new Product(10) , "1");
        assertEquals(1, productService.getAllProducts().size());

    }
}


