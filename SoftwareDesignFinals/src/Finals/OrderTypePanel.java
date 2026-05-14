package Finals;


import javax.swing.*;
import java.awt.*;

public class OrderTypePanel extends JPanel {

    public OrderTypePanel(KioskApp app) {
        setLayout(new GridBagLayout());
        setBackground(Theme.BG_DARK);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Theme.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER, 1),
            BorderFactory.createEmptyBorder(40, 60, 40, 60)));

        JLabel emoji = new JLabel("☕", SwingConstants.CENTER);
        emoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        emoji.setAlignmentX(CENTER_ALIGNMENT);

        JLabel title = new JLabel("Welcome to ChaiWan!", SwingConstants.CENTER);
        title.setFont(Theme.bold(22));
        title.setForeground(Theme.TEXT_MAIN);
        title.setAlignmentX(CENTER_ALIGNMENT);

        JLabel sub = new JLabel("How would you like your order?", SwingConstants.CENTER);
        sub.setFont(Theme.plain(14));
        sub.setForeground(Theme.TEXT_MUTED);
        sub.setAlignmentX(CENTER_ALIGNMENT);

        JButton dineBtn = makeOrderBtn("🍽  Dine In");
        JButton takeBtn = makeOrderBtn("🥤  Take Out");

        dineBtn.addActionListener(e -> { app.setOrderType("Dine In");  app.showPanel(KioskApp.P_MENU); });
        takeBtn.addActionListener(e -> { app.setOrderType("Take Out"); app.showPanel(KioskApp.P_MENU); });

        // Logout link
        JButton logout = new JButton("Logout");
        logout.setBackground(Theme.BG_CARD);
        logout.setForeground(Theme.TEXT_MUTED);
        logout.setFont(Theme.plain(11));
        logout.setBorderPainted(false);
        logout.setFocusPainted(false);
        logout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logout.setAlignmentX(CENTER_ALIGNMENT);
        logout.addActionListener(e -> {
            SwingUtilities.getWindowAncestor(this).dispose();
            new LoginPage();
        });

        card.add(emoji);
        card.add(Box.createVerticalStrut(10));
        card.add(title);
        card.add(sub);
        card.add(Box.createVerticalStrut(30));
        card.add(dineBtn);
        card.add(Box.createVerticalStrut(12));
        card.add(takeBtn);
        card.add(Box.createVerticalStrut(20));
        card.add(logout);

        add(card);
    }

    private JButton makeOrderBtn(String label) {
        JButton b = new JButton(label);
        b.setBackground(Theme.ACCENT);
        b.setForeground(Color.WHITE);
        b.setFont(Theme.bold(16));
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(240, 56));
        b.setMaximumSize(new Dimension(240, 56));
        b.setAlignmentX(CENTER_ALIGNMENT);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }
}
