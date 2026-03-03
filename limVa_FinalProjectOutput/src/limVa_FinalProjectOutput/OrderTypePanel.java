package limVa_FinalProjectOutput;

import javax.swing.*;
import java.awt.*;



public class OrderTypePanel extends JPanel {
    private KioskApp app; 

    
    public OrderTypePanel(KioskApp app) {
        this.app = app;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); 
        add(Box.createVerticalGlue());
        add(new JLabel("Welcome to GingerBrave's Eateria!"));
        add(Box.createVerticalStrut(20));

        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton dineButton = new JButton("Dine In");
        dineButton.setPreferredSize(new Dimension(200, 50)); 
        JButton takeButton = new JButton("Take Out");
        takeButton.setPreferredSize(new Dimension(200, 50)); 

        dineButton.addActionListener(e -> {
            app.setOrderType("dine");
            app.showPanel("Menu");
        });
        takeButton.addActionListener(e -> {
            app.setOrderType("take");
            app.showPanel("Menu");
        });

        buttonPanel.add(dineButton);
        buttonPanel.add(takeButton);
        add(buttonPanel);
        add(Box.createVerticalGlue()); // Bottom spacer
    }
}