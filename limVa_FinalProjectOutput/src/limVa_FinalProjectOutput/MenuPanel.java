package limVa_FinalProjectOutput;

import javax.swing.*;
import java.awt.*;

public class MenuPanel extends JPanel {
    private KioskApp app; 
    private JLabel statusLabel; 

   
    private String[][] menuItems = {
    	{"Burger", "167", "burger.png", "Starter"},
        {"Tomato Soup", "150", "tomato soup.png", "Starter"},
        {"Croissant", "120", "croissant.png", "Starter"},
        
        {"Sirloin Steak", "499", "steak.png", "Main Dish"},
        {"Omurice", "299", "omurice.png", "Main Dish"},
        {"Rose Pasta", "359", "pasta.png", "Main Dish"},
        {"Special Pizza", "399", "pizza.png", "Main Dish"},
        
        {"Glazed Donut", "99", "donut.png", "Dessert"},
        {"Raspberry Pie", "185", "raspberry pie.png", "Dessert"},
        {"Chocolate Cupcake", "120", "cupcake.png", "Dessert"},
        {"Sliced Strawberry Cake", "185", "cake.png", "Dessert"},
        
        {"Coffee", "125", "coffee.png", "Beverage"},
        {"Milk Tea", "145", "boba.png", "Beverage"},
        {"Strawberry Ade", "145", "strawberry ade.png", "Beverage"},
        {"Matcha Latte", "135", "matcha.png", "Beverage"}
    };

    // Constructor: Sets up the panel
    public MenuPanel(KioskApp app) {
        this.app = app;
        setLayout(new BorderLayout()); // Use BorderLayout for centering
        add(Box.createVerticalStrut(10), BorderLayout.NORTH); // Top spacer

        // Tabbed pane for categories
        JTabbedPane tabbedPane = new JTabbedPane();
        String[] categories = {"Starter", "Main Dish", "Dessert", "Beverage"};

        for (String category : categories) {
            JPanel categoryPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Center items in tab
            for (String[] item : menuItems) {
                if (item[3].equals(category)) {
                    String name = item[0];
                    String price = item[1];
                    String imagePath = item[2];

                    // Panel for each item
                    JPanel itemPanel = new JPanel(new BorderLayout());
                    itemPanel.setBorder(BorderFactory.createTitledBorder(name + " - ₱" + price));
                    itemPanel.setMaximumSize(new Dimension(200, 250));

                    // Image button (clicking toggles quantity controls) - Square
                    JButton itemButton = new JButton();
                    itemButton.setPreferredSize(new Dimension(150, 150));
                    itemButton.setLayout(new BorderLayout());
                    try {
                        ImageIcon icon = new ImageIcon(imagePath);
                        if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                            Image scaledImage = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                            itemButton.setIcon(new ImageIcon(scaledImage));
                        } else {
                            System.out.println("Failed to load image: " + imagePath);
                            itemButton.setText("Image not found");
                        }
                    } catch (Exception e) {
                        System.out.println("Error loading image: " + imagePath + " - " + e.getMessage());
                        itemButton.setText("Image not found");
                    }
                    itemButton.add(new JLabel(name + " - ₱" + price), BorderLayout.SOUTH);

                    // Quantity control panel (initially hidden)
                    JPanel qtyPanel = new JPanel(new FlowLayout());
                    JButton minusButton = new JButton("-");
                    JLabel qtyLabel = new JLabel("0");
                    JButton plusButton = new JButton("+");
                    JButton confirmButton = new JButton("Confirm");
                    qtyPanel.add(minusButton);
                    qtyPanel.add(qtyLabel);
                    qtyPanel.add(plusButton);
                    qtyPanel.add(confirmButton);
                    qtyPanel.setVisible(false);

                    // Toggle quantity panel on image click
                    itemButton.addActionListener(e -> {
                        qtyPanel.setVisible(!qtyPanel.isVisible());
                        revalidate();
                        repaint();
                    });

                    minusButton.addActionListener(e -> {
                        int currentQty = Integer.parseInt(qtyLabel.getText());
                        if (currentQty > 0) {
                            qtyLabel.setText(String.valueOf(currentQty - 1));
                        }
                    });

                    plusButton.addActionListener(e -> {
                        int currentQty = Integer.parseInt(qtyLabel.getText());
                        qtyLabel.setText(String.valueOf(currentQty + 1));
                    });

                   
                    confirmButton.addActionListener(e -> {
                        int qty = Integer.parseInt(qtyLabel.getText());
                        if (qty > 0) {
                            double itemPrice = Double.parseDouble(price) * qty;
                            app.getCart().add(new CartItem(name, qty, itemPrice));
                            app.addToTotal(itemPrice);
                            qtyLabel.setText("0");
                            qtyPanel.setVisible(false);
                            app.updateCartDisplay();
                            showStatus("Added to cart!");
                        } else {
                            showStatus("Quantity must be greater than 0.");
                        }
                    });

                    itemPanel.add(itemButton, BorderLayout.NORTH);
                    itemPanel.add(qtyPanel, BorderLayout.CENTER);
                    categoryPanel.add(itemPanel);
                }
            }
            tabbedPane.addTab(category, categoryPanel);
        }

        add(tabbedPane, BorderLayout.CENTER);

        // Status label
        statusLabel = new JLabel("");
        statusLabel.setForeground(Color.RED);
        add(statusLabel, BorderLayout.SOUTH);

        // Bottom buttons panel (centered)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton viewCartButton = new JButton("View Cart");
        viewCartButton.addActionListener(e -> app.showPanel("Cart"));
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> app.showPanel("OrderType"));
        buttonPanel.add(viewCartButton);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void showStatus(String message) {
        statusLabel.setText(message);
        Timer timer = new Timer(2000, e -> statusLabel.setText(""));
        timer.setRepeats(false);
        timer.start();
    }
}