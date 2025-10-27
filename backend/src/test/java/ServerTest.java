
import  static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import main.java.Server;
import main.java.User;
import main.java.UserService;



public class ServerTest {

    @Test
    void testServerHealth() {
        Server server = new Server();
        assertEquals("Server running on http://localhost:8080" , server.start());
    }

    @Test
    void testServerLoginSuccess() {
        UserService userService = new UserService();
        // First register the user
        userService.register("Mandla", "password");
        // Then test login
        assertTrue(Server.loginHandler(userService, new User("Mandla", "password")));
    }

    @Test
    void testServerLoginFailure() {
        UserService userService = new UserService();
        // Try to login with non-existent user
        assertFalse(Server.loginHandler(userService, new User("NonExistentUser", "password")));
    }
    @Test
    void testServerRegisterSuccess() {
        UserService userService = new UserService();
        assertTrue(Server.registerHandler(userService, new User("NewUser", "password")));
    }
    @Test
    void testServerRegisterFailure() {
        UserService userService = new UserService();
        // First register a user
        userService.register("ExistingUser", "password");
        // Try to register the same user again - should fail
        assertFalse(Server.registerHandler(userService, new User("ExistingUser", "password")));
    }
    @Test
    void testServerDeleteProfileSuccess() {
        UserService userService = new UserService();
        // First register a user
        userService.register("UserToDelete", "password");
        // Then test delete
        assertTrue(Server.deleteProfileHandler(userService, new User("UserToDelete", "password")));
    }
    @Test
    void testServerDeleteProfileFailure() {
        UserService userService = new UserService();
        // Try to delete non-existent user - should fail
        assertFalse(Server.deleteProfileHandler(userService, new User("NonExistentUser", "password")));
    }
    @Test
    void testServerUpdateProfileSuccess() {
        UserService userService = new UserService();
        // First register a user
        userService.register("UserToUpdate", "password");
        // Then test update
        assertTrue(Server.updateProfileHandler(userService, new User("UserToUpdate", "newpassword")));
    }
    @Test
    void testServerUpdateProfileFailure() {
        UserService userService = new UserService();
        // Try to update non-existent user - should fail
        assertFalse(Server.updateProfileHandler(userService, new User("NonExistentUser", "password")));
    }
    @Test
    void testServerGetProfileSuccess() {
        UserService userService = new UserService();
        // First register a user
        userService.register("UserToGet", "password");
        // Then test get profile
        assertTrue(Server.getProfileHandler(userService, new User("UserToGet", "password")));
    }
    @Test
    void testServerGetProfileFailure() {
        UserService userService = new UserService();
        // Try to get profile of non-existent user - should fail
        assertFalse(Server.getProfileHandler(userService, new User("NonExistentUser", "password")));
    }

}
