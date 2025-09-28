
import  main.java.UserService;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import main.java.Server;
import main.java.User;



public class ServerTest {

    @Test
    void testServerHealth() {
        Server server = new Server();
        assertEquals("Server running on http://localhost:8080" , server.start());
    }

    @Test
    void testServerLoginSuccess() {
        Server server = new Server();
          assertTrue(server.loginHandler(new UserService(), new User("Mandla", "password")));

    }

    @Test
    void testServerLoginFailure() {
        Server server = new Server();
          assertFalse(server.loginHandler(new UserService(), new User("Mandla", "password")));

    }
}
