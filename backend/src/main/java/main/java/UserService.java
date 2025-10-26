package main.java;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service class for managing user operations
 */
public class UserService {
    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final Map<String, String> activeSessions = new ConcurrentHashMap<>();

    public UserService() {
        // Initialize with a sample user
        initializeSampleData();
    }

    /**
     * Registers a new user
     */
    public boolean register(String username, String password) {
        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            return false;
        }

        String lowerUsername = username.toLowerCase();
        if (users.containsKey(lowerUsername)) {
            return false;
        }

        try {
            User newUser = new User(username, password);
            users.put(lowerUsername, newUser);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Authenticates a user login
     */
    public boolean login(String username, String password) {
        if (username == null || password == null) {
            return false;
        }

        User user = users.get(username.toLowerCase());
        if (user != null && user.isActive() && user.getPassword().equals(password)) {
            // Generate session token
            String sessionToken = generateSessionToken(username);
            activeSessions.put(sessionToken, username.toLowerCase());
            return true;
        }
        return false;
    }

    /**
     * Logs out a user by removing their session
     */
    public boolean logout(String sessionToken) {
        return activeSessions.remove(sessionToken) != null;
    }

    /**
     * Validates a session token
     */
    public boolean isValidSession(String sessionToken) {
        return activeSessions.containsKey(sessionToken);
    }

    /**
     * Gets username from session token
     */
    public String getUsernameFromSession(String sessionToken) {
        return activeSessions.get(sessionToken);
    }

    /**
     * Gets user profile information
     */
    public User getProfile(String username) {
        if (username == null) {
            return null;
        }
        return users.get(username.toLowerCase());
    }

    /**
     * Updates user profile
     */
    public boolean updateProfile(String username, String newPassword) {
        if (username == null || newPassword == null) {
            return false;
        }

        User user = users.get(username.toLowerCase());
        if (user != null) {
            try {
                user.setPassword(newPassword);
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
        return false;
    }

    /**
     * Deletes a user profile (soft delete)
     */
    public boolean deleteProfile(String username) {
        if (username == null) {
            return false;
        }

        User user = users.get(username.toLowerCase());
        if (user != null && user.isActive()) {
            user.setActive(false);
            return true;
        }
        return false;
    }

    /**
     * Gets all users (admin function)
     */
    public List<User> getAllUsers() {
        return users.values().stream()
                .filter(User::isActive)
                .toList();
    }

    /**
     * Generates a session token for authentication
     */
    private String generateSessionToken(String username) {
        try {
            String data = username + System.currentTimeMillis();
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(data.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return username + System.currentTimeMillis();
        }
    }

    /**
     * Hashes a password using SHA-256
     */
    public String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return password;
        }
    }

    private void initializeSampleData() {
        // Add a sample user for testing
        try {
            User sampleUser = new User("admin", "password123");
            users.put("admin", sampleUser);
        } catch (IllegalArgumentException e) {
            // Sample user creation failed, continue without it
        }
    }
}