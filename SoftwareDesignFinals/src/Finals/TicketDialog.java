package Finals;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Ticket/receipt dialog shown to the customer after payment is confirmed.
 * Large centered queue number at the top, items listed below.
 */
public class TicketDialog extends JDialog {

    public TicketDialog(Window owner, Transaction tx) {
        super(owner, "Order Ticket", ModalityType.APPLICATION_MODAL);
        setSize(380, 560);
        setLocationRelativeTo(owner);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG_DARK);
        root.setBorder(BorderFactory.createLineBorder(Theme.BORDER, 1));
        setContentPane(root);

        root.add(buildTicket(tx), BorderLayout.CENTER);
        root.add(buildFooter(),   BorderLayout.SOUTH);
    }

    // ── Ticket body ───────────────────────────────────────────────────────────

    private JPanel buildTicket(Transaction tx) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Theme.BG_CARD);
        p.setBorder(BorderFactory.createEmptyBorder(24, 28, 20, 28));

        // Header
        JLabel shopName = centeredLabel("☕ ChaiWan POS", Theme.TEXT_MAIN, Theme.bold(15));
        JLabel subName  = centeredLabel("Cafe Ingredients Inventory", Theme.TEXT_MUTED, Theme.plain(11));
        JLabel divider1 = dashedLine();

        // ── BIG Queue Number ──────────────────────────────────────────────────
        JLabel queueTitle = centeredLabel("QUEUE NUMBER", Theme.TEXT_MUTED, Theme.bold(12));
        queueTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JLabel queueNum = new JLabel(String.valueOf(tx.getQueueNumber()), SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Glowing circle behind number
                int cx = getWidth() / 2, cy = getHeight() / 2;
                int r  = Math.min(cx, cy) - 4;
                g2.setColor(new Color(99, 179, 237, 35));
                g2.fillOval(cx - r, cy - r, r * 2, r * 2);
                g2.setColor(Theme.ACCENT);
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(cx - r, cy - r, r * 2, r * 2);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        queueNum.setFont(new Font("Segoe UI", Font.BOLD, 72));
        queueNum.setForeground(Theme.ACCENT);
        queueNum.setAlignmentX(CENTER_ALIGNMENT);
        queueNum.setPreferredSize(new Dimension(160, 130));
        queueNum.setMaximumSize(new Dimension(320, 130));

        JLabel orderType = centeredLabel(tx.getOrderType().toUpperCase(),
            tx.getOrderType().equalsIgnoreCase("Dine In") ? Theme.SUCCESS : Theme.WARNING,
            Theme.bold(13));

        JLabel divider2 = dashedLine();

        // ── Items list ────────────────────────────────────────────────────────
        JLabel itemsHeader = leftLabel("ITEMS ORDERED", Theme.TEXT_MUTED, Theme.bold(11));
        itemsHeader.setBorder(BorderFactory.createEmptyBorder(6, 0, 4, 0));

        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setBackground(Theme.BG_CARD);

        for (CartItem ci : tx.getItems()) {
            JPanel row = new JPanel(new BorderLayout(8, 0));
            row.setBackground(Theme.BG_CARD);
            row.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));

            JLabel nameLbl = new JLabel(ci.getQuantity() + "x  " + ci.getName()
                + "  [Sugar: " + ci.getSugarLevel() + "%]");
            nameLbl.setFont(Theme.plain(12));
            nameLbl.setForeground(Theme.TEXT_MAIN);

            JLabel priceLbl = new JLabel("₱" + String.format("%.2f", ci.getTotalPrice()),
                SwingConstants.RIGHT);
            priceLbl.setFont(Theme.plain(12));
            priceLbl.setForeground(Theme.TEXT_MAIN);

            row.add(nameLbl,  BorderLayout.WEST);
            row.add(priceLbl, BorderLayout.EAST);
            itemsPanel.add(row);
        }

        JLabel divider3 = dashedLine();

        // ── Totals ────────────────────────────────────────────────────────────
        JPanel totals = new JPanel(new GridLayout(0, 2, 0, 3));
        totals.setBackground(Theme.BG_CARD);

        addTotalRow(totals, "Subtotal",   "₱" + fmt(tx.getSubtotal()), Theme.TEXT_MUTED);
        addTotalRow(totals, "Tax (12%)",  "₱" + fmt(tx.getTax()),      Theme.TEXT_MUTED);
        addTotalRow(totals, "Discount",  "-₱" + fmt(tx.getDiscount()), Theme.TEXT_MUTED);

        JLabel totalKey = new JLabel("TOTAL");
        totalKey.setFont(Theme.bold(14));
        totalKey.setForeground(Theme.TEXT_MAIN);
        JLabel totalVal = new JLabel("₱" + fmt(tx.getTotal()), SwingConstants.RIGHT);
        totalVal.setFont(Theme.bold(14));
        totalVal.setForeground(Theme.ACCENT);
        totals.add(totalKey); totals.add(totalVal);

        JLabel divider4 = dashedLine();

        // ── Payment / Meta ────────────────────────────────────────────────────
        JPanel meta = new JPanel(new GridLayout(0, 2, 0, 3));
        meta.setBackground(Theme.BG_CARD);
        addTotalRow(meta, "Payment",  tx.getPaymentMethod(), Theme.TEXT_MUTED);
        addTotalRow(meta, "Date",     tx.getFormattedDate(), Theme.TEXT_MUTED);
        addTotalRow(meta, "Served by",tx.getProcessedBy(),   Theme.TEXT_MUTED);

        JLabel thanks = centeredLabel("Thank you for ordering! ☕",
            Theme.ACCENT2, Theme.plain(11));
        thanks.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // ── Assemble ──────────────────────────────────────────────────────────
        p.add(shopName); p.add(subName);
        p.add(divider1);
        p.add(queueTitle);
        p.add(Box.createVerticalStrut(4));
        p.add(queueNum);
        p.add(Box.createVerticalStrut(4));
        p.add(orderType);
        p.add(divider2);
        p.add(itemsHeader);
        p.add(itemsPanel);
        p.add(divider3);
        p.add(totals);
        p.add(divider4);
        p.add(meta);
        p.add(thanks);

        return p;
    }

    private JPanel buildFooter() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        p.setBackground(Theme.BG_DARK);
        JButton closeBtn = Theme.button("Close");
        closeBtn.addActionListener(e -> dispose());
        p.add(closeBtn);
        return p;
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private JLabel centeredLabel(String text, Color color, Font font) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setForeground(color); l.setFont(font);
        l.setAlignmentX(CENTER_ALIGNMENT);
        l.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        return l;
    }

    private JLabel leftLabel(String text, Color color, Font font) {
        JLabel l = new JLabel(text);
        l.setForeground(color); l.setFont(font);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private JLabel dashedLine() {
        JLabel l = new JLabel("- - - - - - - - - - - - - - - - - - - - - -",
            SwingConstants.CENTER);
        l.setFont(Theme.plain(10));
        l.setForeground(Theme.BORDER);
        l.setAlignmentX(CENTER_ALIGNMENT);
        l.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
        l.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        return l;
    }

    private void addTotalRow(JPanel panel, String key, String val, Color color) {
        JLabel k = new JLabel(key);
        k.setFont(Theme.plain(12)); k.setForeground(color);
        JLabel v = new JLabel(val, SwingConstants.RIGHT);
        v.setFont(Theme.plain(12)); v.setForeground(Theme.TEXT_MAIN);
        panel.add(k); panel.add(v);
    }

    private String fmt(double v) { return String.format("%.2f", v); }
}

