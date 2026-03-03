package limVa_FinalProjectOutput;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
  
public class KioskApp extends JFrame {
    private JPanel mainPanel; 
    private String orderType; 
    private List<CartItem> cart; 
    private double total; 

  
    public KioskApp() {
        setTitle("Food Kiosk");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cart = new ArrayList<>();
        total = 0.0;

        
        mainPanel = new JPanel(new CardLayout()); 
        add(mainPanel);

    
        mainPanel.add(new OrderTypePanel(this), "OrderType");
        mainPanel.add(new MenuPanel(this), "Menu");
        mainPanel.add(new CartPanel(this), "Cart");
        mainPanel.add(new PaymentPanel(this), "Payment");
        mainPanel.add(new GcashQR(this), "DigitalWallet"); 

        
        showPanel("OrderType");
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public List<CartItem> getCart() {
        return cart;
    }

    public double getTotal() {
        return total;
    }

    public void addToTotal(double amount) {
        total += amount;
    }

    public void resetOrder() {
        cart.clear();
        total = 0.0;
    }

    public void showPanel(String name) {
        CardLayout cl = (CardLayout) mainPanel.getLayout();
        cl.show(mainPanel, name);
    }

    public void updateCartDisplay() {
        ((CartPanel) mainPanel.getComponent(2)).updateDisplay(); 
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new KioskApp().setVisible(true);
        });
    }
}
