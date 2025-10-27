public class TestServer {
    public static void main(String[] args) {
        System.out.println("Testing server startup...");
        try {
            main.java.Server.main(args);
        } catch (Exception e) {
            System.err.println("Error starting server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
