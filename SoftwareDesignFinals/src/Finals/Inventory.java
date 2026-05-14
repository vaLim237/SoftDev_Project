package Finals;



import java.io.*;
import java.util.*;

/**
 * Manages MenuItem stock, persists to CSV.
 * Pre-loaded with ChaiWan menu items matching the SRS categories.
 */
public class Inventory {

    private static final String FILE = "inventory.csv";
    private Map<String, MenuItem> items = new LinkedHashMap<>();

    public Inventory() {
        loadFromFile(FILE);
        if (items.isEmpty()) seedDefaults();
    }

    // ── CRUD ─────────────────────────────────────────────────────────────────

    public void addItem(MenuItem item) {
        items.put(item.getName(), item);
        saveToFile(FILE);
    }

    public void removeItem(String name) {
        items.remove(name);
        saveToFile(FILE);
    }

    public MenuItem getItem(String name) { return items.get(name); }

    public Collection<MenuItem> getAllItems() { return items.values(); }

    public void reduceStock(String name, int qty) {
        MenuItem item = items.get(name);
        if (item != null) {
            item.reduceStock(qty);
            saveToFile(FILE);
        }
    }

    /** Returns items with stock <= threshold */
    public List<MenuItem> getLowStockItems(int threshold) {
        List<MenuItem> low = new ArrayList<>();
        for (MenuItem item : items.values())
            if (item.getStock() <= threshold) low.add(item);
        return low;
    }

    // ── Persistence ──────────────────────────────────────────────────────────

    public void saveToFile(String filename) {
        try (PrintWriter out = new PrintWriter(new FileWriter(filename))) {
            for (MenuItem item : items.values()) {
                out.println(item.getName() + "," +
                            item.getPrice() + "," +
                            item.getCategory() + "," +
                            item.getStock());
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void loadFromFile(String filename) {
        items.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length == 4) {
                    items.put(p[0], new FoodItem(p[0],
                            Double.parseDouble(p[1]), p[2],
                            Integer.parseInt(p[3])));
                }
            }
        } catch (IOException ignored) {}
    }

    // ── Seed default menu ────────────────────────────────────────────────────

    private void seedDefaults() {
        // Milk Tea
        add("Classic Milk Tea",     89,  "Milk Tea", 50);
        add("Taro Milk Tea",        99,  "Milk Tea", 50);
        add("Brown Sugar Milk Tea", 109, "Milk Tea", 40);
        add("Matcha Milk Tea",      109, "Milk Tea", 40);

        // Coffee
        add("Americano",            79,  "Coffee",   60);
        add("Latte",                99,  "Coffee",   60);
        add("Cappuccino",           99,  "Coffee",   50);
        add("Cold Brew",            109, "Coffee",   40);

        // Fruit Tea
        add("Passion Fruit Tea",    89,  "Fruit Tea", 50);
        add("Lychee Fruit Tea",     89,  "Fruit Tea", 50);
        add("Mango Fruit Tea",      89,  "Fruit Tea", 50);
        add("Strawberry Fruit Tea", 99,  "Fruit Tea", 40);

        saveToFile(FILE);
    }

    private void add(String name, double price, String cat, int stock) {
        items.put(name, new FoodItem(name, price, cat, stock));
    }
}