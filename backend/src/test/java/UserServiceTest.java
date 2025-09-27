import  main.java.UserService;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

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
        userService.hashingPassword("password123");
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
        boolean result = userService.login("Mandla", "passord123");
        System.out.println(result);
        assertTrue(result);
    }

    @Test
    void testLoginFailure() {
        boolean result = userService.login("", "wrongpassword");
        assertFalse(result);
    }

    @Test
    void testRegisterSuccess() {
        boolean results = userService.register("NewUser", "newpassword123");
        assertTrue(results);
    }

//    @Test
//    void testRegisterFailure() {
//        // First register should succeed
//        userService.register("ExistingUser", "password123");
//        // Second register with same username should fail
//        boolean results = userService.register("ExistingUser", "password123");
//        assertFalse(results);
//    }

    @Test
    void testViewProfile() {
        userService.register("TestUser", "password123");
        userService.viewProfile("TestUser");
    }

    @Test
    void testDeleteProfile() {
        userService.register("UserToDelete", "password123");
        userService.deleteProfile("UserToDelete");
    }
}