package Finals;

import javax.swing.*;
import java.awt.*;

public class CartPanel extends JPanel {

    private final KioskApp             app;
    private       DefaultListModel<String> listModel;
    private       JList<String>         cartList;
    private       JLabel               subtotalLabel;
    private       JLabel               taxLabel;
    private       JLabel               totalLabel;
    private       JTextField           discountField;

    public CartPanel(KioskApp app) {
        this.app = app;
        setLayout(new BorderLayout(0, 0));
        setBackground(Theme.BG_DARK);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
        updateDisplay();
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Theme.BG_PANEL);
        p.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        JLabel title = new JLabel("🛒 Your Cart");
        title.setFont(Theme.bold(17));
        title.setForeground(Theme.TEXT_MAIN);
        p.add(title, BorderLayout.WEST);
        return p;
    }

    private JPanel buildCenter() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(Theme.BG_DARK);
        p.setBorder(BorderFactory.createEmptyBorder(10, 14, 0, 14));

        listModel = new DefaultListModel<>();
        cartList  = new JList<>(listModel);
        cartList.setBackground(Theme.BG_CARD);
        cartList.setForeground(Theme.TEXT_MAIN);
        cartList.setFont(Theme.plain(13));
        cartList.setSelectionBackground(Theme.ACCENT);
        cartList.setFixedCellHeight(28);
        JScrollPane scroll = new JScrollPane(cartList);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER, 1));

        // Totals panel
        JPanel totals = new JPanel(new GridLayout(0, 2, 0, 4));
        totals.setBackground(Theme.BG_PANEL);
        totals.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER, 1),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)));

        subtotalLabel = makeValLabel("₱0.00");
        taxLabel      = makeValLabel("₱0.00");
        totalLabel    = makeValLabel("₱0.00");

        discountField = new JTextField("0");
        discountField.setBackground(Theme.FIELD_BG);
        discountField.setForeground(Theme.TEXT_MAIN);
        discountField.setFont(Theme.plain(13));
        discountField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER, 1),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        discountField.addActionListener(e -> recalc());
        discountField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent e) { recalc(); }
        });

        totals.add(muted("Subtotal:"));     totals.add(subtotalLabel);
        totals.add(muted("Tax (12%):"));    totals.add(taxLabel);
        totals.add(muted("Discount (₱):")); totals.add(discountField);
        totals.add(bold("TOTAL:"));         totals.add(totalLabel);

        p.add(scroll,  BorderLayout.CENTER);
        p.add(totals, BorderLayout.SOUTH);
        return p;
    }

    private JPanel buildFooter() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        p.setBackground(Theme.BG_PANEL);

        JButton back    = Theme.button("◀ Back to Menu");
        JButton proceed = Theme.successButton("Proceed to Payment ▶");
        JButton clear   = Theme.dangerButton("🗑 Clear Cart");

        back.addActionListener(e    -> app.showPanel(KioskApp.P_MENU));
        proceed.addActionListener(e -> {
            if (app.getCart().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Your cart is empty!", "Empty Cart", JOptionPane.WARNING_MESSAGE);
                return;
            }
            app.showPanel(KioskApp.P_PAYMENT);
        });
        clear.addActionListener(e -> {
            if (!app.getCart().isEmpty()) {
                int ok = JOptionPane.showConfirmDialog(this,
                    "Clear all items from cart?", "Confirm Clear", JOptionPane.YES_NO_OPTION);
                if (ok == JOptionPane.YES_OPTION) {
                    // restore stock
                    for (CartItem ci : app.getCart())
                        app.getContext().getInventory()
                           .getItem(ci.getName()).setStock(
                               app.getContext().getInventory().getItem(ci.getName()).getStock() + ci.getQuantity());
                    app.resetOrder();
                }
            }
        });

        p.add(back); p.add(clear); p.add(proceed);
        return p;
    }

    // ── Update ────────────────────────────────────────────────────────────────

    public void updateDisplay() {
        listModel.clear();
        if (app.getCart().isEmpty()) {
            listModel.addElement("  (Cart is empty)");
        } else {
            for (CartItem ci : app.getCart()) listModel.addElement("  " + ci);
        }
        recalc();
    }

    private void recalc() {
        double sub      = app.getSubtotal();
        double tax      = sub * TransactionManager.getTaxRate();
        double discount = 0;
        try { discount = Double.parseDouble(discountField.getText().trim()); }
        catch (NumberFormatException ignored) {}
        discount = Math.min(discount, sub);

        subtotalLabel.setText("₱" + String.format("%.2f", sub));
        taxLabel.setText("₱"      + String.format("%.2f", tax));
        totalLabel.setText("₱"    + String.format("%.2f", sub + tax - discount));
        totalLabel.setForeground(Theme.ACCENT);
    }

    /** Called by PaymentPanel to get confirmed discount */
    public double getDiscount() {
        try { return Double.parseDouble(discountField.getText().trim()); }
        catch (NumberFormatException e) { return 0; }
    }

    // ── Label helpers ─────────────────────────────────────────────────────────

    private JLabel makeValLabel(String text) {
        JLabel l = new JLabel(text, SwingConstants.RIGHT);
        l.setFont(Theme.bold(13));
        l.setForeground(Theme.TEXT_MAIN);
        return l;
    }
    private JLabel muted(String t) {
        JLabel l = new JLabel(t); l.setForeground(Theme.TEXT_MUTED); l.setFont(Theme.plain(12)); return l;
    }
    private JLabel bold(String t) {
        JLabel l = new JLabel(t); l.setForeground(Theme.TEXT_MAIN);  l.setFont(Theme.bold(13));  return l;
    }
}
