package main.java;

import java.security.MessageDigest;

public class UserService {


    public  boolean login(String name, String password) {
         return true;

    }

    public boolean register(String name, String password) {
       return  true;
    }
   public  String hashingPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);


            }
            return hexString.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;

        }

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