package limVa_FinalProjectOutput;

import javax.swing.*;
import java.awt.*;


public class GcashQR extends JPanel {
    private KioskApp app; 

    
    public GcashQR(KioskApp app) {
        this.app = app;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); 
        add(Box.createVerticalGlue()); 
        add(new JLabel("Scan QR Code for Digital Wallet"));
        add(Box.createVerticalStrut(20));

        
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel imageLabel = new JLabel();
        try {
            ImageIcon icon = new ImageIcon("gcash.jpg");
            if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                Image scaledImage = icon.getImage().getScaledInstance(200, 300, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(scaledImage));
            } else {
                System.out.println("Failed to load wallet image: GCash QR");
                imageLabel.setText("QR Code not available");
            }
        } catch (Exception e) {
            System.out.println("Error loading wallet image: " + e.getMessage());
            imageLabel.setText("QR Code not available");
        }

        JButton confirmButton = new JButton("Confirm Payment");
        confirmButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, "Order confirmed! Payment: Digital Wallet\nTotal: ₱" + String.format("%.2f", app.getTotal()) + "\nPlease Proceed to Counter!");
            app.resetOrder();
            app.updateCartDisplay();
            app.showPanel("OrderType");
        });

        centerPanel.add(imageLabel);
        centerPanel.add(confirmButton); 
        add(centerPanel);
        add(Box.createVerticalStrut(20));

        
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> app.showPanel("Payment"));
        backPanel.add(backButton);
        add(backPanel);
        add(Box.createVerticalGlue()); 
    }
}

