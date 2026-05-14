package Finals;

public class CartItem {
    private String name;
    private int quantity;
    private double unitPrice;   // price per single item (before qty multiply)
    private int sugarLevel;     // 0, 25, 50, 75, 100

    public CartItem(String name, int quantity, double unitPrice, int sugarLevel) {
        this.name      = name;
        this.quantity  = quantity;
        this.unitPrice = unitPrice;
        this.sugarLevel = sugarLevel;
    }

    public String getName()      { return name; }
    public int getQuantity()     { return quantity; }
    public double getUnitPrice() { return unitPrice; }
    public int getSugarLevel()   { return sugarLevel; }

    /** Total price for this line (unitPrice * qty) */
    public double getTotalPrice() { return unitPrice * quantity; }

    @Override
    public String toString() {
        return quantity + "x " + name +
               " [Sugar:" + sugarLevel + "%]" +
               " — ₱" + String.format("%.2f", getTotalPrice());
    }
}
