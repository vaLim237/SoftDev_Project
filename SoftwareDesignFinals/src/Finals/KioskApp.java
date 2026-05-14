package Finals;



import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The customer-facing kiosk window.
 * Baristas use this to place orders, process payment, view cart.
 */
public class KioskApp extends JFrame {

    private static final long serialVersionUID = 1L;

    private final AppContext    ctx;
    private final JPanel        mainPanel;

    private String              orderType  = "Dine In";
    private final List<CartItem> cart      = new ArrayList<>();
    private double              subtotal   = 0.0;

    // panel component positions in mainPanel
    static final String P_ORDER_TYPE = "OrderType";
    static final String P_MENU       = "Menu";
    static final String P_CART       = "Cart";
    static final String P_PAYMENT    = "Payment";
    static final String P_GCASH_QR   = "DigitalWallet";

    public KioskApp(AppContext ctx) {
        this.ctx = ctx;
        setTitle("ChaiWan Kiosk — " + ctx.getLoggedInUser());
        setSize(700, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        mainPanel = new JPanel(new CardLayout());
        mainPanel.setBackground(Theme.BG_DARK);
        add(mainPanel);

        mainPanel.add(new OrderTypePanel(this), P_ORDER_TYPE);
        mainPanel.add(new MenuPanel(this),      P_MENU);
        mainPanel.add(new CartPanel(this),      P_CART);
        mainPanel.add(new PaymentPanel(this),   P_PAYMENT);
        mainPanel.add(new GcashQR(this),        P_GCASH_QR);

        showPanel(P_ORDER_TYPE);
    }

    // ── Navigation ────────────────────────────────────────────────────────────
    public void showPanel(String name) {
        ((CardLayout) mainPanel.getLayout()).show(mainPanel, name);
    }

    // ── Cart helpers ──────────────────────────────────────────────────────────
    public List<CartItem> getCart()     { return cart; }
    public double         getSubtotal() { return subtotal; }
    public String         getOrderType(){ return orderType; }
    public void           setOrderType(String t) { orderType = t; }
    public AppContext      getContext()  { return ctx; }

    public void addToCart(CartItem item) {
        cart.add(item);
        subtotal += item.getTotalPrice();
        refreshCart();
    }

    public void resetOrder() {
        cart.clear();
        subtotal = 0.0;
        refreshCart();
    }

    /** Tells the CartPanel to rebuild its display list */
    public void refreshCart() {
        for (Component c : mainPanel.getComponents())
            if (c instanceof CartPanel) ((CartPanel) c).updateDisplay();
    }

    /** Called after payment is confirmed. */
    public void finaliseOrder(String paymentMethod, double discountAmount) {
        if (!cart.isEmpty()) {
            ctx.getTxManager().createTransaction(
                new ArrayList<>(cart), discountAmount,
                paymentMethod, orderType, ctx.getLoggedInUser());
        }
        resetOrder();
        showPanel(P_ORDER_TYPE);
    }

    /** Low-stock alert shown to barista after an item is added */
    public void checkLowStock() {
        var low = ctx.getInventory().getLowStockItems(5);
        if (!low.isEmpty()) {
            StringBuilder sb = new StringBuilder("⚠ Low Stock Alert:\n");
            for (MenuItem m : low)
                sb.append("  • ").append(m.getName()).append(" (").append(m.getStock()).append(" left)\n");
            JOptionPane.showMessageDialog(this, sb.toString(),
                "Low Stock", JOptionPane.WARNING_MESSAGE);
        }
    }
}
