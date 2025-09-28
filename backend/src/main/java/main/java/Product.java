package main.java;

/***
 * allow to set stock
 * allow to set price
 * allow to update quantity
 *
 *
 */
public class Product {
        int stock;

        public  Product(int stock){
            this.stock = stock;
        }


    public  int getStock() {
            return stock;
    }
    public  void  setStock(int stock) {
        this.stock = stock;
    }
}

