package main.java;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents a product in the e-commerce system.
 * Includes inventory management and product details.
 */
public class Product {
    private final String id;
    private String name;
    private String description;
    private double price;
    private int stock;
    private String category;
    private String imageUrl;
    private double rating;
    private int reviewCount;
    private boolean active;

    /**
     * Creates a new product with the given details.
     * 
     * @param name        The name of the product
     * @param description A description of the product
     * @param price       The price of the product (must be positive)
     * @param stock       The initial stock quantity (must not be negative)
     * @param category    The product category
     * @throws IllegalArgumentException if price is negative or stock is negative
     */
    public Product(String name, String description, double price, int stock, String category) {
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }
        
        this.id = UUID.randomUUID().toString();
        this.name = name != null ? name.trim() : "";
        this.description = description != null ? description.trim() : "";
        this.price = price;
        this.stock = stock;
        this.category = category != null ? category.trim() : "Uncategorized";
        this.active = true;
        this.rating = 0.0;
        this.reviewCount = 0;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public String getCategory() {
        return category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public double getRating() {
        return rating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public boolean isActive() {
        return active;
    }

    // Setters with validation
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
        this.name = name.trim();
    }

    public void setDescription(String description) {
        this.description = description != null ? description.trim() : "";
    }

    public void setPrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        this.price = price;
    }

    public void setStock(int stock) {
        if (stock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }
        this.stock = stock;
    }

    public void setCategory(String category) {
        this.category = category != null ? category.trim() : "Uncategorized";
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Adds stock to the current inventory.
     * 
     * @param quantity The quantity to add (must be positive)
     * @return The new stock level
     * @throws IllegalArgumentException if quantity is not positive
     */
    public int addStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.stock += quantity;
        return this.stock;
    }

    /**
     * Removes stock from the inventory.
     * 
     * @param quantity The quantity to remove (must be positive and not exceed current stock)
     * @return The new stock level
     * @throws IllegalArgumentException if quantity is invalid
     * @throws IllegalStateException if there's not enough stock
     */
    public int removeStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (quantity > this.stock) {
            throw new IllegalStateException("Insufficient stock");
        }
        this.stock -= quantity;
        return this.stock;
    }

    /**
     * Updates the product's rating based on a new review.
     * 
     * @param newRating The new rating to add (0.0 to 5.0)
     * @throws IllegalArgumentException if the rating is out of range
     */
    public void addRating(double newRating) {
        if (newRating < 0 || newRating > 5.0) {
            throw new IllegalArgumentException("Rating must be between 0.0 and 5.0");
        }
        
        // Calculate new average rating
        double totalRating = this.rating * this.reviewCount + newRating;
        this.reviewCount++;
        this.rating = totalRating / this.reviewCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id.equals(product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", stock=" + stock +
                ", category='" + category + '\'' +
                ", active=" + active +
                '}';
    }
}
