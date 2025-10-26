package main.java;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for managing products in the e-commerce system.
 * Handles product CRUD operations, inventory management, and product search.
 */
public class ProductService {
    private final Map<String, Product> products = new HashMap<>();
    private final Map<String, List<String>> categoryIndex = new HashMap<>();

    /**
     * Retrieves all active products
     * @return List of active products
     */
    public List<Product> getAllProducts() {
        return products.values().stream()
                .filter(Product::isActive)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all products including inactive ones (admin only)
     * @return List of all products
     */
    public List<Product> getAllProductsIncludingInactive() {
        return new ArrayList<>(products.values());
    }

    /**
     * Gets a product by its ID
     * @param id The product ID
     * @return The product, or null if not found
     */
    public Product getProductById(String id) {
        if (id == null) return null;
        Product product = products.get(id);
        return (product != null && product.isActive()) ? product : null;
    }

    /**
     * Adds a new product to the system
     * @param product The product to add
     * @return The added product with generated ID
     * @throws IllegalArgumentException if product is null or invalid
     */
    public Product addProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        
        products.put(product.getId(), product);
        
        // Update category index
        categoryIndex.computeIfAbsent(product.getCategory().toLowerCase(), k -> new ArrayList<>())
                   .add(product.getId());
        
        return product;
    }

    /**
     * Updates an existing product
     * @param id The ID of the product to update
     * @param updatedProduct The updated product data
     * @return The updated product, or null if not found
     */
    public Product updateProduct(String id, Product updatedProduct) {
        if (id == null || updatedProduct == null || !products.containsKey(id)) {
            return null;
        }
        
        // Preserve the ID and update other fields
        updatedProduct = new Product(
            updatedProduct.getName(),
            updatedProduct.getDescription(),
            updatedProduct.getPrice(),
            updatedProduct.getStock(),
            updatedProduct.getCategory()
        );
        
        products.put(id, updatedProduct);
        return updatedProduct;
    }

    /**
     * Updates a product's stock level
     * @param id The product ID
     * @param newStock The new stock level
     * @return true if updated successfully, false if product not found
     */
    public boolean updateProductStock(String id, int newStock) {
        Product product = getProductById(id);
        if (product != null) {
            product.setStock(newStock);
            return true;
        }
        return false;
    }

    /**
     * Adds stock to a product
     * @param id The product ID
     * @param quantity The quantity to add
     * @return The new stock level, or -1 if product not found
     */
    public int addStock(String id, int quantity) {
        Product product = getProductById(id);
        if (product != null) {
            return product.addStock(quantity);
        }
        return -1;
    }

    /**
     * Removes stock from a product
     * @param id The product ID
     * @param quantity The quantity to remove
     * @return The new stock level, or -1 if product not found or insufficient stock
     */
    public int removeStock(String id, int quantity) {
        Product product = getProductById(id);
        if (product != null) {
            try {
                return product.removeStock(quantity);
            } catch (IllegalStateException | IllegalArgumentException e) {
                return -1;
            }
        }
        return -1;
    }

    /**
     * Deactivates a product (soft delete)
     * @param id The product ID
     * @return true if deactivated, false if not found
     */
    public boolean deactivateProduct(String id) {
        Product product = getProductById(id);
        if (product != null) {
            product.setActive(false);
            return true;
        }
        return false;
    }

    /**
     * Reactivates a product
     * @param id The product ID
     * @return true if reactivated, false if not found
     */
    public boolean reactivateProduct(String id) {
        Product product = products.get(id); // Include inactive products
        if (product != null) {
            product.setActive(true);
            return true;
        }
        return false;
    }

    /**
     * Permanently deletes a product
     * @param id The product ID
     * @return true if deleted, false if not found
     */
    public boolean deleteProduct(String id) {
        Product product = products.get(id); // Include inactive products
        if (product != null) {
            // Remove from category index
            String category = product.getCategory().toLowerCase();
            if (categoryIndex.containsKey(category)) {
                categoryIndex.get(category).remove(id);
                if (categoryIndex.get(category).isEmpty()) {
                    categoryIndex.remove(category);
                }
            }
            products.remove(id);
            return true;
        }
        return false;
    }

    /**
     * Searches products by name or description
     * @param query The search query
     * @return List of matching products
     */
    public List<Product> searchProducts(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllProducts();
        }
        
        String searchTerm = query.toLowerCase();
        return products.values().stream()
                .filter(Product::isActive)
                .filter(p -> p.getName().toLowerCase().contains(searchTerm) || 
                            p.getDescription().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
    }

    /**
     * Gets products by category
     * @param category The category to filter by
     * @return List of products in the category
     */
    public List<Product> getProductsByCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return getAllProducts();
        }
        
        return products.values().stream()
                .filter(Product::isActive)
                .filter(p -> p.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    /**
     * Gets all available categories
     * @return Set of category names
     */
    public Set<String> getAllCategories() {
        return products.values().stream()
                .filter(Product::isActive)
                .map(Product::getCategory)
                .collect(Collectors.toSet());
    }

    /**
     * Gets featured products (e.g., top-rated or on sale)
     * @param limit Maximum number of products to return
     * @return List of featured products
     */
    public List<Product> getFeaturedProducts(int limit) {
        return products.values().stream()
                .filter(Product::isActive)
                .sorted((p1, p2) -> Double.compare(p2.getRating(), p1.getRating()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Adds a review/rating to a product
     * @param productId The product ID
     * @param rating The rating (0.0 to 5.0)
     * @return true if successful, false if product not found or invalid rating
     */
    public boolean addProductReview(String productId, double rating) {
        try {
            Product product = getProductById(productId);
            if (product != null) {
                product.addRating(rating);
                return true;
            }
        } catch (IllegalArgumentException e) {
            return false;
        }
        return false;
    }
}
