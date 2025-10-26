package main.java;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Represents a user in the system with authentication details
 */
public class User {
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$");
    
    private String username;
    private String password;
    private String email;
    private boolean isActive;

    public User(String username, String password) {
        setUsername(username);
        setPassword(password);
        this.isActive = true;
    }

    public User(String username, String password, String email) {
        this(username, password);
        setEmail(email);
    }

    // Getters and Setters with validation
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (username == null || !USERNAME_PATTERN.matcher(username).matches()) {
            throw new IllegalArgumentException(
                "Username must be 3-20 characters long and can only contain letters, numbers, and underscores");
        }
        this.username = username.trim();
    }

    public String getPassword() {
        return password; // In a real application, never return the actual password
    }

    public void setPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        // Note: In a real application, you would hash the password here
        this.password = password.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email != null && !email.isEmpty()) {
            // Simple email validation
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                throw new IllegalArgumentException("Invalid email format");
            }
        }
        this.email = email != null ? email.trim() : null;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    /**
     * Validates the user's password against the password policy
     * @return true if password meets the policy requirements
     */
    public boolean isPasswordValid() {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return username.equalsIgnoreCase(user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username.toLowerCase());
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", email='" + (email != null ? email : "not set") + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
