package limVa_FinalProjectOutput;

import javax.swing.*;
import java.awt.*;



public class CartPanel extends JPanel {
    private KioskApp app; 
    private JList<String> cartList;
    private DefaultListModel<String> listModel;
    private JLabel totalLabel;

    
    public CartPanel(KioskApp app) {
        this.app = app;
        setLayout(new BorderLayout());
        add(Box.createVerticalGlue(), BorderLayout.NORTH); 
        add(Box.createVerticalGlue(), BorderLayout.SOUTH); 

        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        listModel = new DefaultListModel<>();
        cartList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(cartList);
        scrollPane.setPreferredSize(new Dimension(300, 150));

        totalLabel = new JLabel("Total: ₱0.00");

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> app.showPanel("Menu"));

        JButton proceedButton = new JButton("Proceed to Payment");
        proceedButton.addActionListener(e -> app.showPanel("Payment"));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(backButton);
        buttonPanel.add(proceedButton);

        centerPanel.add(new JLabel("Your Cart:"));
        centerPanel.add(scrollPane);
        centerPanel.add(totalLabel);
        centerPanel.add(buttonPanel);

        add(centerPanel, BorderLayout.CENTER);
    }

    public void updateDisplay() {
        listModel.clear();
        for (CartItem item : app.getCart()) {
            listModel.addElement(item.toString());
        }
        totalLabel.setText("Total: ₱" + String.format("%.2f", app.getTotal()));
    }
}

