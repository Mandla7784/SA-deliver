import org.junit.jupiter.api.Test;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import static org.junit.jupiter.api.Assertions.*;

public class ServerTests {

    @Test
    void testPasswordHashConsistency() throws NoSuchAlgorithmException {
        String userPassword = "password123";

        // Create SHA-256 digest
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(userPassword.getBytes());

        // Convert byte array to hex string
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        String hashedPassword = hexString.toString();

        // Assert against expected SHA-256 hash
        assertEquals("ef92b778bafe771e89245b89ecbcf29c9fa4c70b4e6f4b4f7a22e9f2f3f7f0e5", hashedPassword);
    }
}
