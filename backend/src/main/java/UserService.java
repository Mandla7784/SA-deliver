package main.java;

public class UserService {


    public  boolean login(String name, String password) {
         return true;

    }

    public void register(String name, String password) {
        System.out.println("User registered successfully");
    }

    public void viewProfile(String name) {
        System.out.println("Viewing profile of " + name);

    }

    public void updateProfile(String name, String password) {
        System.out.println("Updating profile of " + name);


    }
    public  void deleteProfile(String name){
        System.out.println("Deleting profile of " + name);

    }
    public  void viewOrderHistory(String name){
        System.out.println("Viewing order history of " + name);

    }
    public  void placeOrder(String name){
        System.out.println("Placing order for " + name);

    }
}