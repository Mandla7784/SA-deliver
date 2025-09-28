package main.java;

import com.google.gson.Gson;
import spark.Spark;

import static spark.Spark.*;
import com.google.gson.Gson;

/**
 * Here on the server im gonna be handling and adding
 * components for handling business logic and data management
 * such as a web server to serve content,
 * a database to store product and customer information,
 * a payment gateway integration for secure transaction
 *order processing
 *
 */


public class Server {
    public static void main(String[] args) {
        Gson gson = new Gson();
        UserService userService = new UserService();

        start();
        get("/health", (req, res) -> "Server is running");


        post("/register", (req, res) -> {
            res.type("application/json");
            User user = gson.fromJson(req.body(), User.class);
            boolean success = userService.register(user.username, user.password);
            return gson.toJson(new Response(success, success ? "User registered" : "User exists"));

        });

        post("/login" ,(req, res)-> {
            res.type("application/json");
            User user = gson.fromJson(req.body(), User.class);
            boolean success;
            success = userService.login(user.username, user.password);
            return gson.toJson(new Response(success, success ? "Login successful" : "Invalid credentials"));
        });
      get("/users/:username" ,(req, res)-> {
             String username = req.params("username");
             return   gson.toJson(userService.viewProfile(username));

      });

    }
    public static String start() {
        return "Server running on http://localhost:8080";
    }

    public static  boolean loginHandler(UserService userService ,User user) {
          return  userService.login(user.username , user.password);

    }
}
