package main.java;
import static spark.Spark.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main server class handling HTTP requests and business logic
 */
public class Server {
    private static final Gson gson = new Gson();
    private static UserService userService;
    private static ProductService productService;
    private static final Map<String, String> sessionTokens = new HashMap<>();

    public static void main(String[] args) {
        userService = new UserService();
        productService = new ProductService();
        
        port(8080);

        // Enable CORS for frontend integration
        enableCORS();
        
        // Initialize database with sample data
        initializeSampleData();

        // Health check
        get("/health", (req, res) -> {
            res.type("application/json");
            return gson.toJson(new Response(true, "Server is running"));
        });

        // Authentication endpoints
        setupAuthEndpoints();
        
        // Product endpoints
        setupProductEndpoints();
        
        // User profile endpoints
        setupUserEndpoints();
        
        // Error handling
        setupErrorHandling();
    }

    private static void enableCORS() {
        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.header("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With");
        });

        options("/*", (request, response) -> {
            response.status(200);
            return "OK";
        });
    }

    private static void setupAuthEndpoints() {
        // Register user
        post("/api/register", (req, res) -> {
            res.type("application/json");
            try {
                JsonObject json = JsonParser.parseString(req.body()).getAsJsonObject();
                String username = json.get("username").getAsString();
                String password = json.get("password").getAsString();
                String email = json.has("email") ? json.get("email").getAsString() : null;
                
                User user = new User(username, password);
                if (email != null && !email.trim().isEmpty()) {
                    user.setEmail(email);
                }
                boolean success = userService.register(username, password);
                
                if (success) {
                    return gson.toJson(new Response(true, "User registered successfully", user));
                } else {
                    return gson.toJson(new Response(false, "Username already exists"));
                }
            } catch (Exception e) {
                return gson.toJson(new Response(false, "Registration failed: " + e.getMessage()));
            }
        });

        // Login user
        post("/api/login", (req, res) -> {
            res.type("application/json");
            try {
                JsonObject json = JsonParser.parseString(req.body()).getAsJsonObject();
                String username = json.get("username").getAsString();
                String password = json.get("password").getAsString();
                
                boolean success = userService.login(username, password);
                
                if (success) {
                    // Generate session token
                    String sessionToken = generateSessionToken(username);
                    sessionTokens.put(sessionToken, username);
                    
                    Map<String, Object> loginData = new HashMap<>();
                    loginData.put("sessionToken", sessionToken);
                    loginData.put("username", username);
                    
                    return gson.toJson(new Response(true, "Login successful", loginData));
                } else {
                    return gson.toJson(new Response(false, "Invalid credentials"));
                }
            } catch (Exception e) {
                return gson.toJson(new Response(false, "Login failed: " + e.getMessage()));
            }
        });

        // Logout user
        post("/api/logout", (req, res) -> {
            res.type("application/json");
            try {
                String sessionToken = req.headers("Authorization");
                if (sessionToken != null && sessionToken.startsWith("Bearer ")) {
                    sessionToken = sessionToken.substring(7);
                    sessionTokens.remove(sessionToken);
                    return gson.toJson(new Response(true, "Logout successful"));
                }
                return gson.toJson(new Response(false, "No active session"));
            } catch (Exception e) {
                return gson.toJson(new Response(false, "Logout failed: " + e.getMessage()));
            }
        });
    }

    private static void setupProductEndpoints() {
        // Get all products
        get("/api/products", (req, res) -> {
            res.type("application/json");
            try {
                List<Product> products = productService.getAllProducts();
                return gson.toJson(new Response(true, "Products retrieved successfully", products));
            } catch (Exception e) {
                return gson.toJson(new Response(false, "Failed to retrieve products: " + e.getMessage()));
            }
        });

        // Get product by ID
        get("/api/products/:id", (req, res) -> {
            res.type("application/json");
            try {
                String id = req.params("id");
                Product product = productService.getProductById(id);
                
                if (product != null) {
                    return gson.toJson(new Response(true, "Product retrieved successfully", product));
                } else {
                    return gson.toJson(new Response(false, "Product not found"));
                }
            } catch (Exception e) {
                return gson.toJson(new Response(false, "Failed to retrieve product: " + e.getMessage()));
            }
        });

        // Search products
        get("/api/products/search/:query", (req, res) -> {
            res.type("application/json");
            try {
                String query = req.params("query");
                List<Product> products = productService.searchProducts(query);
                return gson.toJson(new Response(true, "Search completed", products));
            } catch (Exception e) {
                return gson.toJson(new Response(false, "Search failed: " + e.getMessage()));
            }
        });

        // Get products by category
        get("/api/products/category/:category", (req, res) -> {
            res.type("application/json");
            try {
                String category = req.params("category");
                List<Product> products = productService.getProductsByCategory(category);
                return gson.toJson(new Response(true, "Products retrieved successfully", products));
            } catch (Exception e) {
                return gson.toJson(new Response(false, "Failed to retrieve products: " + e.getMessage()));
            }
        });

        // Get all categories
        get("/api/categories", (req, res) -> {
            res.type("application/json");
            try {
                List<String> categories = productService.getAllCategories().stream().toList();
                return gson.toJson(new Response(true, "Categories retrieved successfully", categories));
            } catch (Exception e) {
                return gson.toJson(new Response(false, "Failed to retrieve categories: " + e.getMessage()));
            }
        });

        // Add product (admin only)
        post("/api/products", (req, res) -> {
            res.type("application/json");
            try {
                if (!isAuthenticated(req)) {
                    return gson.toJson(new Response(false, "Authentication required"));
                }
                
                Product product = gson.fromJson(req.body(), Product.class);
                Product addedProduct = productService.addProduct(product);
                return gson.toJson(new Response(true, "Product added successfully", addedProduct));
            } catch (Exception e) {
                return gson.toJson(new Response(false, "Failed to add product: " + e.getMessage()));
            }
        });

        // Update product (admin only)
        put("/api/products/:id", (req, res) -> {
            res.type("application/json");
            try {
                if (!isAuthenticated(req)) {
                    return gson.toJson(new Response(false, "Authentication required"));
                }
                
                String id = req.params("id");
                Product product = gson.fromJson(req.body(), Product.class);
                Product updatedProduct = productService.updateProduct(id, product);
                
                if (updatedProduct != null) {
                    return gson.toJson(new Response(true, "Product updated successfully", updatedProduct));
                } else {
                    return gson.toJson(new Response(false, "Product not found"));
                }
            } catch (Exception e) {
                return gson.toJson(new Response(false, "Failed to update product: " + e.getMessage()));
            }
        });

        // Delete product (admin only)
        delete("/api/products/:id", (req, res) -> {
            res.type("application/json");
            try {
                if (!isAuthenticated(req)) {
                    return gson.toJson(new Response(false, "Authentication required"));
                }
                
                String id = req.params("id");
                boolean deleted = productService.deleteProduct(id);
                
                if (deleted) {
                    return gson.toJson(new Response(true, "Product deleted successfully"));
                } else {
                    return gson.toJson(new Response(false, "Product not found"));
                }
            } catch (Exception e) {
                return gson.toJson(new Response(false, "Failed to delete product: " + e.getMessage()));
            }
        });
    }

    private static void setupUserEndpoints() {
        // Get user profile
        get("/api/profile", (req, res) -> {
            res.type("application/json");
            try {
                String username = getUsernameFromRequest(req);
                if (username == null) {
                    return gson.toJson(new Response(false, "Authentication required"));
                }
                
                User user = userService.getProfile(username);
                if (user != null) {
                    // Don't return password
                    user.setPassword(null);
                    return gson.toJson(new Response(true, "Profile retrieved successfully", user));
                } else {
                    return gson.toJson(new Response(false, "User not found"));
                }
            } catch (Exception e) {
                return gson.toJson(new Response(false, "Failed to retrieve profile: " + e.getMessage()));
            }
        });

        // Update user profile
        put("/api/profile", (req, res) -> {
            res.type("application/json");
            try {
                String username = getUsernameFromRequest(req);
                if (username == null) {
                    return gson.toJson(new Response(false, "Authentication required"));
                }
                
                JsonObject json = JsonParser.parseString(req.body()).getAsJsonObject();
                String newPassword = json.has("password") ? json.get("password").getAsString() : null;
                
                boolean updated = userService.updateProfile(username, newPassword);
                
                if (updated) {
                    return gson.toJson(new Response(true, "Profile updated successfully"));
                } else {
                    return gson.toJson(new Response(false, "Failed to update profile"));
                }
            } catch (Exception e) {
                return gson.toJson(new Response(false, "Failed to update profile: " + e.getMessage()));
            }
        });

        // Delete user profile
        delete("/api/profile", (req, res) -> {
            res.type("application/json");
            try {
                String username = getUsernameFromRequest(req);
                if (username == null) {
                    return gson.toJson(new Response(false, "Authentication required"));
                }
                
            boolean deleted = userService.deleteProfile(username);
                
                if (deleted) {
                    return gson.toJson(new Response(true, "Profile deleted successfully"));
                } else {
                    return gson.toJson(new Response(false, "Failed to delete profile"));
                }
            } catch (Exception e) {
                return gson.toJson(new Response(false, "Failed to delete profile: " + e.getMessage()));
            }
        });
    }

    private static void setupErrorHandling() {
        exception(Exception.class, (exception, request, response) -> {
            response.type("application/json");
            response.status(500);
            response.body(gson.toJson(new Response(false, "Internal server error: " + exception.getMessage())));
        });

        notFound((request, response) -> {
            response.type("application/json");
            response.status(404);
            return gson.toJson(new Response(false, "Endpoint not found"));
        });
    }

    private static void initializeSampleData() {
        try {
            // Add sample products if none exist
            if (productService.getAllProducts().isEmpty()) {
                Product product1 = new Product("Laptop", "High-performance laptop for professionals", 17999.82, 10, "Electronics");
                Product product2 = new Product("Smartphone", "Latest smartphone with advanced features", 12599.82, 25, "Electronics");
                Product product3 = new Product("Coffee Maker", "Automatic coffee maker for home use", 2699.82, 15, "Appliances");
                Product product4 = new Product("Running Shoes", "Comfortable running shoes for athletes", 1619.82, 30, "Sports");
                
                productService.addProduct(product1);
                productService.addProduct(product2);
                productService.addProduct(product3);
                productService.addProduct(product4);
            }
        } catch (Exception e) {
            System.err.println("Failed to initialize sample data: " + e.getMessage());
        }
    }

    private static boolean isAuthenticated(spark.Request req) {
        String authHeader = req.headers("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String sessionToken = authHeader.substring(7);
            return sessionTokens.containsKey(sessionToken);
        }
        return false;
    }

    private static String getUsernameFromRequest(spark.Request req) {
        String authHeader = req.headers("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String sessionToken = authHeader.substring(7);
            return sessionTokens.get(sessionToken);
        }
        return null;
    }

    private static String generateSessionToken(String username) {
        return username + "_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
    }

    // Test methods for unit testing
    public static boolean loginHandler(UserService userService, User user) {
        return user.getUsername() != null && user.getPassword() != null &&
                userService.login(user.getUsername(), user.getPassword());
    }

    public static boolean registerHandler(UserService userService, User user) {
        return userService.register(user.getUsername(), user.getPassword());
    }

    public static boolean deleteProfileHandler(UserService userService, User user) {
        return userService.deleteProfile(user.getUsername());
    }

    public static boolean updateProfileHandler(UserService userService, User user) {
        return userService.updateProfile(user.getUsername(), user.getPassword());
    }

    public static boolean getProfileHandler(UserService userService, User user) {
        return userService.getProfile(user.getUsername()) != null;
    }

    public String start() {
        return "Server running on http://localhost:8080";
    }

    public static void main(String[] args) {
        // Get port from environment variable (Railway sets this)
        String port = System.getenv("PORT");
        if (port != null) {
            port(Integer.parseInt(port));
        } else {
            port(8080); // Default port for local development
        }

        // Initialize services
        userService = new UserService();
        productService = new ProductService();

        // Setup routes
        setupRoutes();
        setupErrorHandling();
        initializeSampleData();

        // Start server
        System.out.println("SA-Deliver server started on port " + (port != null ? port : "8080"));
    }
}