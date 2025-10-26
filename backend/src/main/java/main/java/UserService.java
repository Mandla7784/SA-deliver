    public  boolean deleteProfile(String name){
        if (name == null) {
            return false;
        }
        User user = users.get(name.toLowerCase());
        if (user != null && user.isActive()) {
            users.remove(name.toLowerCase());
            return true;
        }
        return false;


    }
    public boolean getProfile(String name){
        System.out.println("Getting profile of " + name);
        return true;
    }
    public  void viewOrderHistory(String name){
        System.out.println("Viewing order history of " + name);

    }
    public  void placeOrder(String name){
        System.out.println("Placing order for " + name);
    }
}