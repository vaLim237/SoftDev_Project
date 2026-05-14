package Finals;

import javax.swing.*;
import java.awt.*;

public class PaymentPanel extends JPanel {

    private final KioskApp app;

    public PaymentPanel(KioskApp app) {
        this.app = app;
        setLayout(new GridBagLayout());
        setBackground(Theme.BG_DARK);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Theme.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER, 1),
            BorderFactory.createEmptyBorder(36, 52, 36, 52)));

        JLabel title = new JLabel("Select Payment Method", SwingConstants.CENTER);
        title.setFont(Theme.bold(18));
        title.setForeground(Theme.TEXT_MAIN);
        title.setAlignmentX(CENTER_ALIGNMENT);

        JLabel sub = new JLabel("How will you be paying?", SwingConstants.CENTER);
        sub.setFont(Theme.plain(13));
        sub.setForeground(Theme.TEXT_MUTED);
        sub.setAlignmentX(CENTER_ALIGNMENT);

        JButton cashBtn  = payBtn("💵  Cash");
        JButton gcashBtn = payBtn("📱  GCash");
        JButton mayaBtn  = payBtn("💳  Maya");
        JButton backBtn  = Theme.button("◀ Back");
        backBtn.setAlignmentX(CENTER_ALIGNMENT);

        cashBtn.addActionListener(e  -> confirmPayment("Cash"));
        mayaBtn.addActionListener(e  -> confirmPayment("Maya"));
        gcashBtn.addActionListener(e -> app.showPanel(KioskApp.P_GCASH_QR));
        backBtn.addActionListener(e  -> app.showPanel(KioskApp.P_CART));

        card.add(title);
        card.add(Box.createVerticalStrut(6));
        card.add(sub);
        card.add(Box.createVerticalStrut(28));
        card.add(cashBtn);
        card.add(Box.createVerticalStrut(12));
        card.add(gcashBtn);
        card.add(Box.createVerticalStrut(12));
        card.add(mayaBtn);
        card.add(Box.createVerticalStrut(22));
        card.add(backBtn);

        add(card);
    }

    /** Called by both this panel (Cash/Maya) and GcashQR (GCash). */
    void confirmPayment(String method) {
        double discount = getDiscountFromCart();

        // Record transaction — writes to CSV, updates TransactionManager list
        Transaction tx = app.getContext().getTxManager().createTransaction(
            app.getCart(), discount, method,
            app.getOrderType(), app.getContext().getLoggedInUser());

        // Clear cart first so CartPanel shows empty if reopened
        app.resetOrder();

        // Show styled ticket dialog (blocks until user dismisses)
        Window owner = SwingUtilities.getWindowAncestor(this);
        new TicketDialog(owner, tx).setVisible(true);

        // Return to order-type screen
        app.showPanel(KioskApp.P_ORDER_TYPE);

        // Tell AppContext to push the new data to any open dashboard
        app.getContext().notifyDashboard();
    }

    private double getDiscountFromCart() {
        Container parent = this.getParent();
        if (parent != null)
            for (Component c : parent.getComponents())
                if (c instanceof CartPanel) return ((CartPanel) c).getDiscount();
        return 0;
    }

    private JButton payBtn(String label) {
        JButton b = new JButton(label);
        b.setBackground(Theme.ACCENT);
        b.setForeground(Color.WHITE);
        b.setFont(Theme.bold(15));
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(220, 50));
        b.setMaximumSize(new Dimension(220, 50));
        b.setAlignmentX(CENTER_ALIGNMENT);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }
}

