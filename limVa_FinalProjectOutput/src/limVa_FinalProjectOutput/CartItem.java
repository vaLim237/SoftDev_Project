package limVa_FinalProjectOutput;


public class CartItem {
 private String name;
 private int quantity;
 private double price;

 
 public CartItem(String name, int quantity, double price) {
     this.name = name;
     this.quantity = quantity;
     this.price = price;
 }

 public String getName() {
     return name;
 }

 
 public int getQuantity() {
     return quantity;
 }

 
 public double getPrice() {
     return price;
 }

// Polymorphism 
 @Override
 public String toString() {
     return quantity + " x " + name + " - ₱" + String.format("%.2f", price);
 }
}
