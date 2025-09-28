package main.java;

import  java.util.HashMap;
import  java.util.ArrayList;
import java.util.List;

public class ProductService {
    HashMap<String, Product> products = new HashMap<>();
    // we need a list to hold products for now
    // loop on the list and return the product
    // we need tp get product by id
    //  allows us to add products
    // allows to update products stocks
  public List<Product> getAllProducts() {
      return new ArrayList<>(products.values());
  }

  public Product getProductById(String id) {
      return  products.get(id);
  }

  public void addProduct(Product product , String id) {
       products.put(id , product);


  }
  public  void updateProductStocks(String id , int quantity) {
      products.get(id).setStock(quantity);

  }

  public  void  deleteProduct(String id) {
      products.remove(id);
  }
}
