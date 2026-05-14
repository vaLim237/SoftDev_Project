package Finals;

import javax.swing.*;
import java.awt.*;

public class GcashQR extends JPanel {

    private final KioskApp app;

    public GcashQR(KioskApp app) {
        this.app = app;
        setLayout(new GridBagLayout());
        setBackground(Theme.BG_DARK);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Theme.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER, 1),
            BorderFactory.createEmptyBorder(32, 48, 32, 48)));

        JLabel title = new JLabel("📱 GCash Payment", SwingConstants.CENTER);
        title.setFont(Theme.bold(18));
        title.setForeground(Theme.TEXT_MAIN);
        title.setAlignmentX(CENTER_ALIGNMENT);

        JLabel instruction = new JLabel("Scan QR code to pay:", SwingConstants.CENTER);
        instruction.setFont(Theme.plain(13));
        instruction.setForeground(Theme.TEXT_MUTED);
        instruction.setAlignmentX(CENTER_ALIGNMENT);

        // QR image or dark-themed placeholder
        JLabel qrLabel = loadQRLabel();

        JButton confirmBtn = Theme.successButton("✓  I've Paid via GCash");
        confirmBtn.setAlignmentX(CENTER_ALIGNMENT);
        confirmBtn.addActionListener(e -> {
            PaymentPanel pp = findPaymentPanel();
            if (pp != null) pp.confirmPayment("GCash");
            else {
                // Fallback: create tx manually
                double discount = getDiscountFromCart();
                Transaction tx = app.getContext().getTxManager().createTransaction(
                    app.getCart(), discount, "GCash",
                    app.getOrderType(), app.getContext().getLoggedInUser());
                app.resetOrder();
                Window owner = SwingUtilities.getWindowAncestor(this);
                new TicketDialog(owner, tx).setVisible(true);
                app.showPanel(KioskApp.P_ORDER_TYPE);
                app.getContext().notifyDashboard();
            }
        });

        JButton backBtn = Theme.button("◀ Back");
        backBtn.setAlignmentX(CENTER_ALIGNMENT);
        backBtn.addActionListener(e -> app.showPanel(KioskApp.P_PAYMENT));

        card.add(title);
        card.add(Box.createVerticalStrut(6));
        card.add(instruction);
        card.add(Box.createVerticalStrut(16));
        card.add(qrLabel);
        card.add(Box.createVerticalStrut(20));
        card.add(confirmBtn);
        card.add(Box.createVerticalStrut(10));
        card.add(backBtn);

        add(card);
    }

    private JLabel loadQRLabel() {
        JLabel lbl;
        try {
            ImageIcon icon = new ImageIcon("gcash.jpg");
            if (icon.getIconWidth() > 0) {
                Image scaled = icon.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH);
                lbl = new JLabel(new ImageIcon(scaled));
            } else throw new Exception("missing");
        } catch (Exception ex) {
            lbl = buildPlaceholderQR();
        }
        lbl.setAlignmentX(CENTER_ALIGNMENT);
        return lbl;
    }

    private JLabel buildPlaceholderQR() {
        JLabel lbl = new JLabel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                // Dark background with light border
                g2.setColor(Theme.FIELD_BG);
                g2.fillRect(0, 0, 180, 180);
                g2.setColor(Theme.BORDER);
                g2.drawRect(0, 0, 179, 179);
                // Fake QR pixel blocks in accent colour
                g2.setColor(Theme.ACCENT);
                int[][] blk = {{10,10},{10,50},{50,10},{120,10},{120,50},{10,120},{50,120},{80,80}};
                for (int[] b : blk) g2.fillRect(b[0], b[1], 40, 40);
                g2.setColor(Theme.BG_CARD);
                for (int[] b : blk) g2.fillRect(b[0]+8, b[1]+8, 24, 24);
                // Label
                g2.setColor(Theme.TEXT_MUTED);
                g2.setFont(Theme.plain(9));
                g2.drawString("Place gcash.jpg in project root", 8, 170);
            }
        };
        lbl.setPreferredSize(new Dimension(180, 180));
        lbl.setMaximumSize(new Dimension(180, 180));
        return lbl;
    }

    private PaymentPanel findPaymentPanel() {
        Container parent = this.getParent();
        if (parent != null)
            for (Component c : parent.getComponents())
                if (c instanceof PaymentPanel) return (PaymentPanel) c;
        return null;
    }

    private double getDiscountFromCart() {
        Container parent = this.getParent();
        if (parent != null)
            for (Component c : parent.getComponents())
                if (c instanceof CartPanel) return ((CartPanel) c).getDiscount();
        return 0;
    }
}
