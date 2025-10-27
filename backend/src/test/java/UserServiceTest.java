import  java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import main.java.UserService;

/**
 * 
 */
public class UserServiceTest {
    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService();
    }

    @Test
    void testPasswordHashConsistency() throws NoSuchAlgorithmException {
        String hashedPassword = userService.hashPassword("password123");
        assertNotNull(hashedPassword);
        assertFalse(hashedPassword.isEmpty());
    }

    @Test
    void testDifferentPasswordsHaveDifferentHashes() throws NoSuchAlgorithmException {
        String password1 = "password123";
        String password2 = "password456";

        MessageDigest md = MessageDigest.getInstance("SHA-256");

        byte[] hash1 = md.digest(password1.getBytes(StandardCharsets.UTF_8));
        byte[] hash2 = md.digest(password2.getBytes(StandardCharsets.UTF_8));

        StringBuilder hex1 = new StringBuilder();
        for (byte b : hash1) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hex1.append('0');
            hex1.append(hex);
        }

        StringBuilder hex2 = new StringBuilder();
        for (byte b : hash2) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hex2.append('0');
            hex2.append(hex);
        }

        assertNotEquals(hex1.toString(), hex2.toString());
    }

    @Test
    void testLoginSuccess() {
        // First register a user
        userService.register("Mandla", "passord123");
        // Then try to login
        boolean result = userService.login("Mandla", "passord123");
        assertTrue(result);
    }

    @Test
    void testLoginFailure() {
        boolean result = userService.login(null,  null);
        assertFalse(result);
    }

    @Test
    void testRegisterSuccess() {
        boolean results = userService.register("NewUser", "newpassword123");
        assertTrue(results);
    }

    @Test
    void testRegisterFailure() {
        userService.register(null  ,null);
        boolean results = userService.register(null, null);
        assertFalse(results);
    }

    @Test
    void testViewProfile() {
        userService.register("TestUser", "password123");
        var profile = userService.getProfile("TestUser");
        assertNotNull(profile);
        assertEquals("TestUser", profile.getUsername());
    }

    @Test
    void testDeleteProfile() {
        userService.register("UserToDelete", "password123");
        boolean result = userService.deleteProfile("UserToDelete");
        assertTrue(result);
    }
}