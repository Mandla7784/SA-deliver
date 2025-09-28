package main.java;
import static spark.Spark.*;
import com.google.gson.Gson;

/**
 * Handles business logic and data management
 * such as user registration, login, and profile handling.
 */

public class Server {
    public static void main(String[] args) {
        Gson gson = new Gson();
        UserService userService = new UserService();

        // Health check
        get("/health", (req, res) -> "Server is running");

        // Register user
        post("/register", (req, res) -> {
            res.type("application/json");
            User user = gson.fromJson(req.body(), User.class);
            boolean success = userService.register(user.getUsername(), user.getPassword());
            return gson.toJson(new Response(success, success ? "User registered" : "User exists"));
        });

        // Login user
        post("/login", (req, res) -> {
            res.type("application/json");
            User user = gson.fromJson(req.body(), User.class);
            boolean success = userService.login(user.getUsername(), user.getPassword());
            return gson.toJson(new Response(success, success ? "Login successful" : "Invalid credentials"));
        });

        // Get user by username
        get("/users/:username", (req, res) -> {
            String username = req.params("username");
            boolean found = userService.getProfile(username);
            return gson.toJson(new Response(found, found ? "User found" : "User not found"));
        });

        // Delete user
        delete("/users/:username", (req, res) -> {
            String username = req.params("username");
            boolean deleted = userService.deleteProfile(username);
            return gson.toJson(new Response(deleted, deleted ? "User deleted" : "User not found"));
        });
    }

 public  String start(){
        return  "Server running on http://localhost:8080";
 }
    public static boolean loginHandler(UserService userService, User user) {
        return user.getUsername() != null && user.getUsername() != null &&
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
        return userService.getProfile(user.getUsername());
    }
}
