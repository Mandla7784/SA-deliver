package main.java;

import main.java.entities.User;
import main.java.util.DatabaseUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service class for managing user operations with database persistence
 */
public class UserService {
    private final SessionFactory sessionFactory;
    private final Map<String, String> activeSessions = new ConcurrentHashMap<>();

    public UserService() {
        this.sessionFactory = DatabaseUtil.getSessionFactory();
    }

    /**
     * Registers a new user
     */
    public boolean register(String username, String password) {
        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            return false;
        }

        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                // Check if user already exists
                Query<User> query = session.createQuery("FROM User u WHERE u.username = :username", User.class);
                query.setParameter("username", username.toLowerCase());
                User existingUser = query.uniqueResult();
                
                if (existingUser != null) {
                    return false;
                }

                // Create new user
                User newUser = new User(username, password);
                session.persist(newUser);
                transaction.commit();
                return true;
            } catch (Exception e) {
                transaction.rollback();
                return false;
            }
        }
    }

    /**
     * Authenticates a user login
     */
    public boolean login(String username, String password) {
        if (username == null || password == null) {
            return false;
        }

        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("FROM User u WHERE u.username = :username AND u.active = true", User.class);
            query.setParameter("username", username.toLowerCase());
            User user = query.uniqueResult();
            
            if (user != null && user.getPassword().equals(password)) {
                // Generate session token
                String sessionToken = generateSessionToken(username);
                activeSessions.put(sessionToken, username.toLowerCase());
                return true;
            }
            return false;
        }
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

        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("FROM User u WHERE u.username = :username", User.class);
            query.setParameter("username", username.toLowerCase());
            return query.uniqueResult();
        }
    }

    /**
     * Updates user profile
     */
    public boolean updateProfile(String username, String newPassword) {
        if (username == null || newPassword == null) {
            return false;
        }

        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                Query<User> query = session.createQuery("FROM User u WHERE u.username = :username", User.class);
                query.setParameter("username", username.toLowerCase());
                User user = query.uniqueResult();
                
                if (user != null) {
                    user.setPassword(newPassword);
                    session.merge(user);
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
     * Deletes a user profile (soft delete)
     */
    public boolean deleteProfile(String username) {
        if (username == null) {
            return false;
        }

        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                Query<User> query = session.createQuery("FROM User u WHERE u.username = :username", User.class);
                query.setParameter("username", username.toLowerCase());
                User user = query.uniqueResult();
                
                if (user != null) {
                    user.setActive(false);
                    session.merge(user);
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
     * Gets all users (admin function)
     */
    public List<User> getAllUsers() {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("FROM User", User.class);
            return query.list();
        }
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
}