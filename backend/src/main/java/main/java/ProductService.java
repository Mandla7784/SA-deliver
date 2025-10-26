package main.java;

import main.java.entities.Product;
import main.java.util.DatabaseUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for managing products in the e-commerce system.
 * Handles product CRUD operations, inventory management, and product search.
 */
public class ProductService {
    private final SessionFactory sessionFactory;

    public ProductService() {
        this.sessionFactory = DatabaseUtil.getSessionFactory();
    }

    /**
     * Retrieves all active products
     * @return List of active products
     */
    public List<Product> getAllProducts() {
        try (Session session = sessionFactory.openSession()) {
            Query<Product> query = session.createQuery("FROM Product p WHERE p.active = true", Product.class);
            return query.list();
        }
    }

    /**
     * Retrieves all products including inactive ones (admin only)
     * @return List of all products
     */
    public List<Product> getAllProductsIncludingInactive() {
        try (Session session = sessionFactory.openSession()) {
            Query<Product> query = session.createQuery("FROM Product", Product.class);
            return query.list();
        }
    }

    /**
     * Gets a product by its ID
     * @param id The product ID
     * @return The product, or null if not found
     */
    public Product getProductById(String id) {
        if (id == null) return null;
        
        try (Session session = sessionFactory.openSession()) {
            return session.get(Product.class, id);
        }
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
        
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                session.persist(product);
                transaction.commit();
                return product;
            } catch (Exception e) {
                transaction.rollback();
                throw new RuntimeException("Failed to add product", e);
            }
        }
    }

    /**
     * Updates an existing product
     * @param id The ID of the product to update
     * @param updatedProduct The updated product data
     * @return The updated product, or null if not found
     */
    public Product updateProduct(String id, Product updatedProduct) {
        if (id == null || updatedProduct == null) {
            return null;
        }
        
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                Product existingProduct = session.get(Product.class, id);
                if (existingProduct != null) {
                    existingProduct.setName(updatedProduct.getName());
                    existingProduct.setDescription(updatedProduct.getDescription());
                    existingProduct.setPrice(updatedProduct.getPrice());
                    existingProduct.setStock(updatedProduct.getStock());
                    existingProduct.setCategory(updatedProduct.getCategory());
                    session.merge(existingProduct);
                    transaction.commit();
                    return existingProduct;
                }
                return null;
            } catch (Exception e) {
                transaction.rollback();
                return null;
            }
        }
    }

    /**
     * Updates a product's stock level
     * @param id The product ID
     * @param newStock The new stock level
     * @return true if updated successfully, false if product not found
     */
    public boolean updateProductStock(String id, int newStock) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                Product product = session.get(Product.class, id);
                if (product != null) {
                    product.setStock(newStock);
                    session.merge(product);
                    transaction.commit();
                    return true;
                }
                return false;
            } catch (Exception e) {
                transaction.rollback();
                return false;
            }
        }
    }

    /**
     * Adds stock to a product
     * @param id The product ID
     * @param quantity The quantity to add
     * @return The new stock level, or -1 if product not found
     */
    public int addStock(String id, int quantity) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                Product product = session.get(Product.class, id);
                if (product != null) {
                    int newStock = product.getStock() + quantity;
                    product.setStock(newStock);
                    session.merge(product);
                    transaction.commit();
                    return newStock;
                }
                return -1;
            } catch (Exception e) {
                transaction.rollback();
                return -1;
            }
        }
    }

    /**
     * Removes stock from a product
     * @param id The product ID
     * @param quantity The quantity to remove
     * @return The new stock level, or -1 if product not found or insufficient stock
     */
    public int removeStock(String id, int quantity) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                Product product = session.get(Product.class, id);
                if (product != null && product.getStock() >= quantity) {
                    int newStock = product.getStock() - quantity;
                    product.setStock(newStock);
                    session.merge(product);
                    transaction.commit();
                    return newStock;
                }
                return -1;
            } catch (Exception e) {
                transaction.rollback();
                return -1;
            }
        }
    }

    /**
     * Deactivates a product (soft delete)
     * @param id The product ID
     * @return true if deactivated, false if not found
     */
    public boolean deactivateProduct(String id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                Product product = session.get(Product.class, id);
                if (product != null) {
                    product.setActive(false);
                    session.merge(product);
                    transaction.commit();
                    return true;
                }
                return false;
            } catch (Exception e) {
                transaction.rollback();
                return false;
            }
        }
    }

    /**
     * Reactivates a product
     * @param id The product ID
     * @return true if reactivated, false if not found
     */
    public boolean reactivateProduct(String id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                Product product = session.get(Product.class, id);
                if (product != null) {
                    product.setActive(true);
                    session.merge(product);
                    transaction.commit();
                    return true;
                }
                return false;
            } catch (Exception e) {
                transaction.rollback();
                return false;
            }
        }
    }

    /**
     * Permanently deletes a product
     * @param id The product ID
     * @return true if deleted, false if not found
     */
    public boolean deleteProduct(String id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                Product product = session.get(Product.class, id);
                if (product != null) {
                    session.remove(product);
                    transaction.commit();
                    return true;
                }
                return false;
            } catch (Exception e) {
                transaction.rollback();
                return false;
            }
        }
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
        
        try (Session session = sessionFactory.openSession()) {
            Query<Product> hqlQuery = session.createQuery(
                "FROM Product p WHERE p.active = true AND (LOWER(p.name) LIKE :query OR LOWER(p.description) LIKE :query)", 
                Product.class
            );
            hqlQuery.setParameter("query", "%" + query.toLowerCase() + "%");
            return hqlQuery.list();
        }
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
        
        try (Session session = sessionFactory.openSession()) {
            Query<Product> query = session.createQuery(
                "FROM Product p WHERE p.active = true AND LOWER(p.category) = :category", 
                Product.class
            );
            query.setParameter("category", category.toLowerCase());
            return query.list();
        }
    }

    /**
     * Gets all available categories
     * @return Set of category names
     */
    public Set<String> getAllCategories() {
        try (Session session = sessionFactory.openSession()) {
            Query<String> query = session.createQuery(
                "SELECT DISTINCT p.category FROM Product p WHERE p.active = true", 
                String.class
            );
            return new HashSet<>(query.list());
        }
    }

    /**
     * Gets featured products (e.g., top-rated or on sale)
     * @param limit Maximum number of products to return
     * @return List of featured products
     */
    public List<Product> getFeaturedProducts(int limit) {
        try (Session session = sessionFactory.openSession()) {
            Query<Product> query = session.createQuery(
                "FROM Product p WHERE p.active = true ORDER BY p.rating DESC", 
                Product.class
            );
            query.setMaxResults(limit);
            return query.list();
        }
    }

    /**
     * Adds a review/rating to a product
     * @param productId The product ID
     * @param rating The rating (0.0 to 5.0)
     * @return true if successful, false if product not found or invalid rating
     */
    public boolean addProductReview(String productId, double rating) {
        if (rating < 0.0 || rating > 5.0) {
            return false;
        }
        
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                Product product = session.get(Product.class, productId);
                if (product != null) {
                    product.addRating(rating);
                    session.merge(product);
                    transaction.commit();
                    return true;
                }
                return false;
            } catch (Exception e) {
                transaction.rollback();
                return false;
            }
        }
    }
}
