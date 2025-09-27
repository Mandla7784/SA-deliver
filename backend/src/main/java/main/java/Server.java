package main.java;

import com.google.gson.Gson;
import spark.Spark;



import static spark.Spark.*;
import com.google.gson.Gson;

public class Server {
    public static void main(String[] args) {
        Gson gson = new Gson();
        UserService userService = new UserService();

        System.out.println("Server running on http://localhost:8080");
        get("/health", (req, res) -> "Server is running");


        post("/register", (req, res) -> {
            res.type("application/json");
            User user = gson.fromJson(req.body(), User.class);
            boolean success = userService.register(user.username, user.password);
            return gson.toJson(new Response(success, success ? "User registered" : "User exists"));

        });


    }
}
