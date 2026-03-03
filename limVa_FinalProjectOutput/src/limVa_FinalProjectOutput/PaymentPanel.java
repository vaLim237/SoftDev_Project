package limVa_FinalProjectOutput;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;



public class PaymentPanel extends JPanel {
    private KioskApp app; 

    
    public PaymentPanel(KioskApp app) {
        this.app = app;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); 
        add(Box.createVerticalGlue()); 
        add(new JLabel("Select Payment Method:"));
        add(Box.createVerticalStrut(20));

        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton cashButton = new JButton("Cash");
        JButton cardButton = new JButton("Card");
        JButton walletButton = new JButton("Digital Wallet");

        ActionListener paymentListener = e -> {
            String method = ((JButton) e.getSource()).getText();
            if ("Digital Wallet".equals(method)) {
                app.showPanel("DigitalWallet"); 
            } else {
                confirmPayment(method);
            }
        };

        cashButton.addActionListener(paymentListener);
        cardButton.addActionListener(paymentListener);
        walletButton.addActionListener(paymentListener);

        buttonPanel.add(cashButton);
        buttonPanel.add(cardButton);
        buttonPanel.add(walletButton);
        add(buttonPanel);

        
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> app.showPanel("Cart"));
        backPanel.add(backButton);
        add(backPanel);
        add(Box.createVerticalGlue()); 
    }

    private void confirmPayment(String method) {
        JOptionPane.showMessageDialog(null, "Order confirmed! Payment: " + method + "\nTotal: ₱" + String.format("%.2f", app.getTotal()) + "\nPlease Proceed to Counter!");
        app.resetOrder();
        app.updateCartDisplay();
        app.showPanel("OrderType");
    }
}
