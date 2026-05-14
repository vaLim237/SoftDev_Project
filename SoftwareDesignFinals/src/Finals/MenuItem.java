package Finals;



public abstract class MenuItem {
    private String name;
    private double price;
    private String category;
    private int stock;

    public MenuItem(String name, double price, String category, int stock) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.stock = stock;
    }

    public String getName()     { return name; }
    public double getPrice()    { return price; }
    public String getCategory() { return category; }
    public int getStock()       { return stock; }

    public void reduceStock(int qty) {
        stock = Math.max(0, stock - qty);
    }

    public void setPrice(double price)       { this.price = price; }
    public void setCategory(String category) { this.category = category; }
    public void setStock(int stock)          { this.stock = stock; }

    @Override
    public String toString() {
        return name + " (" + category + ") — ₱" + String.format("%.2f", price) + " | Stock: " + stock;
    }
}
