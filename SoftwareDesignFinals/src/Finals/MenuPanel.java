package Finals;

import javax.swing.*;
import java.awt.*;

/**
 * Menu browser with category tabs, sugar-level customisation, and search.
 * Categories match SRS: Milk Tea, Coffee, Fruit Tea.
 */
public class MenuPanel extends JPanel {

    private final KioskApp   app;
    private       JTabbedPane tabs;
    private       JTextField  searchField;
    private       JLabel      statusLabel;

    // Sugar level options from SRS F2
    private static final int[] SUGAR = {0, 25, 50, 75, 100};

    public MenuPanel(KioskApp app) {
        this.app = app;
        setLayout(new BorderLayout(0, 0));
        setBackground(Theme.BG_DARK);

        add(buildTopBar(),   BorderLayout.NORTH);
        buildTabs("");
        add(buildBottomBar(), BorderLayout.SOUTH);
    }

    // ── Top bar ───────────────────────────────────────────────────────────────

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout(10, 0));
        bar.setBackground(Theme.BG_PANEL);
        bar.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        JLabel title = new JLabel("☕ Menu");
        title.setFont(Theme.bold(16));
        title.setForeground(Theme.TEXT_MAIN);

        searchField = new JTextField();
        searchField.setBackground(Theme.FIELD_BG);
        searchField.setForeground(Theme.TEXT_MAIN);
        searchField.setCaretColor(Theme.ACCENT);
        searchField.setFont(Theme.plain(13));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER, 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        searchField.setColumns(16);

        JButton searchBtn  = Theme.button("🔍 Search");
        JButton refreshBtn = Theme.button("🔄 Refresh");

        searchBtn.addActionListener(e  -> buildTabs(searchField.getText().toLowerCase()));
        refreshBtn.addActionListener(e -> { buildTabs(""); showStatus("Stock refreshed!"); });

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        right.setBackground(Theme.BG_PANEL);
        right.add(searchField);
        right.add(searchBtn);
        right.add(refreshBtn);

        statusLabel = new JLabel(" ");
        statusLabel.setFont(Theme.plain(11));
        statusLabel.setForeground(Theme.SUCCESS);

        bar.add(title, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        bar.add(statusLabel, BorderLayout.SOUTH);
        return bar;
    }

    // ── Bottom nav bar ────────────────────────────────────────────────────────

    private JPanel buildBottomBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        bar.setBackground(Theme.BG_PANEL);

        JButton viewCart = Theme.button("🛒 View Cart");
        JButton back     = Theme.button("◀ Back");

        viewCart.addActionListener(e -> app.showPanel(KioskApp.P_CART));
        back.addActionListener(e     -> app.showPanel(KioskApp.P_ORDER_TYPE));

        bar.add(back);
        bar.add(viewCart);
        return bar;
    }

    // ── Tabs ──────────────────────────────────────────────────────────────────

    private void buildTabs(String query) {
        if (tabs != null) remove(tabs);

        tabs = new JTabbedPane();
        tabs.setBackground(Theme.BG_CARD);
        tabs.setForeground(Theme.TEXT_MAIN);
        tabs.setFont(Theme.bold(13));

        String[] categories = {"Milk Tea", "Coffee", "Fruit Tea"};
        for (String cat : categories) {
            JPanel catPanel = new JPanel(new WrapLayout(FlowLayout.LEFT, 12, 12));
            catPanel.setBackground(Theme.BG_DARK);
            JScrollPane scroll = new JScrollPane(catPanel);
            scroll.setBackground(Theme.BG_DARK);
            scroll.getViewport().setBackground(Theme.BG_DARK);
            scroll.setBorder(BorderFactory.createEmptyBorder());

            boolean hasItems = false;
            for (MenuItem item : app.getContext().getInventory().getAllItems()) {
                if (!item.getCategory().equals(cat)) continue;
                if (!item.getName().toLowerCase().contains(query)) continue;
                catPanel.add(buildItemCard(item));
                hasItems = true;
            }

            if (!hasItems) {
                JLabel empty = new JLabel("No items found.", SwingConstants.CENTER);
                empty.setForeground(Theme.TEXT_MUTED);
                empty.setFont(Theme.plain(13));
                catPanel.add(empty);
            }

            tabs.addTab(cat, scroll);
        }

        add(tabs, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    // ── Item card ─────────────────────────────────────────────────────────────

    private JPanel buildItemCard(MenuItem item) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Theme.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER, 1),
            BorderFactory.createEmptyBorder(12, 14, 12, 14)));
        card.setPreferredSize(new Dimension(200, 210));

        JLabel nameLabel = new JLabel(item.getName());
        nameLabel.setFont(Theme.bold(13));
        nameLabel.setForeground(Theme.TEXT_MAIN);
        nameLabel.setAlignmentX(LEFT_ALIGNMENT);

        JLabel priceLabel = new JLabel("₱" + String.format("%.2f", item.getPrice()));
        priceLabel.setFont(Theme.bold(14));
        priceLabel.setForeground(Theme.ACCENT);
        priceLabel.setAlignmentX(LEFT_ALIGNMENT);

        boolean outOfStock = item.getStock() <= 0;
        JLabel stockLabel = new JLabel("Stock: " + item.getStock());
        stockLabel.setFont(Theme.plain(11));
        stockLabel.setForeground(outOfStock ? Theme.DANGER : (item.getStock() <= 5 ? Theme.WARNING : Theme.TEXT_MUTED));
        stockLabel.setAlignmentX(LEFT_ALIGNMENT);

        // Sugar level selector
        JLabel sugarLbl = new JLabel("Sugar:");
        sugarLbl.setFont(Theme.plain(11));
        sugarLbl.setForeground(Theme.TEXT_MUTED);
        sugarLbl.setAlignmentX(LEFT_ALIGNMENT);

        String[] sugarOptions = {"0%", "25%", "50%", "75%", "100%"};
        JComboBox<String> sugarCombo = new JComboBox<>(sugarOptions);
        sugarCombo.setSelectedIndex(2); // default 50%
        sugarCombo.setBackground(Theme.FIELD_BG);
        sugarCombo.setForeground(Theme.TEXT_MAIN);
        sugarCombo.setFont(Theme.plain(11));
        sugarCombo.setMaximumSize(new Dimension(170, 28));
        sugarCombo.setAlignmentX(LEFT_ALIGNMENT);

        // Quantity row
        JPanel qtyRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        qtyRow.setBackground(Theme.BG_CARD);
        qtyRow.setAlignmentX(LEFT_ALIGNMENT);
        JButton minus = smallBtn("−");
        JLabel  qtyLbl = new JLabel("1");
        qtyLbl.setFont(Theme.bold(13));
        qtyLbl.setForeground(Theme.TEXT_MAIN);
        JButton plus  = smallBtn("+");
        qtyRow.add(minus); qtyRow.add(qtyLbl); qtyRow.add(plus);

        minus.addActionListener(e -> {
            int v = Integer.parseInt(qtyLbl.getText());
            if (v > 1) qtyLbl.setText(String.valueOf(v - 1));
        });
        plus.addActionListener(e -> {
            int v = Integer.parseInt(qtyLbl.getText());
            qtyLbl.setText(String.valueOf(v + 1));
        });

        // Add to cart button
        JButton addBtn = outOfStock ? Theme.dangerButton("Out of Stock") : Theme.successButton("Add to Cart");
        addBtn.setEnabled(!outOfStock);
        addBtn.setAlignmentX(LEFT_ALIGNMENT);
        addBtn.setMaximumSize(new Dimension(170, 32));
        addBtn.addActionListener(e -> {
            int qty   = Integer.parseInt(qtyLbl.getText());
            int sugar = SUGAR[sugarCombo.getSelectedIndex()];

            if (item.getStock() < qty) {
                JOptionPane.showMessageDialog(this,
                    "Only " + item.getStock() + " units available.",
                    "Insufficient Stock", JOptionPane.WARNING_MESSAGE);
                return;
            }

            CartItem ci = new CartItem(item.getName(), qty, item.getPrice(), sugar);
            app.getContext().getInventory().reduceStock(item.getName(), qty);
            app.addToCart(ci);
            showStatus("Added: " + item.getName() + " x" + qty);
            qtyLbl.setText("1");

            // refresh card to show updated stock
            buildTabs(searchField.getText().toLowerCase());

            // low-stock alert
            app.checkLowStock();
        });

        card.add(nameLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(priceLabel);
        card.add(stockLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(sugarLbl);
        card.add(sugarCombo);
        card.add(Box.createVerticalStrut(6));
        card.add(qtyRow);
        card.add(Box.createVerticalStrut(6));
        card.add(addBtn);

        return card;
    }

    private JButton smallBtn(String t) {
        JButton b = new JButton(t);
        b.setBackground(Theme.FIELD_BG);
        b.setForeground(Theme.TEXT_MAIN);
        b.setFont(Theme.bold(14));
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(30, 30));
        b.setBorder(BorderFactory.createLineBorder(Theme.BORDER, 1));
        return b;
    }

    private void showStatus(String msg) {
        statusLabel.setText(msg);
        Timer t = new Timer(2500, e -> statusLabel.setText(" "));
        t.setRepeats(false);
        t.start();
    }

    // ── Simple wrap layout inner class ────────────────────────────────────────
    /** FlowLayout that wraps to next line. */
    static class WrapLayout extends FlowLayout {
        WrapLayout(int align, int hgap, int vgap) { super(align, hgap, vgap); }
        @Override public Dimension preferredLayoutSize(Container target) {
            return layoutSize(target, true);
        }
        @Override public Dimension minimumLayoutSize(Container target) {
            return layoutSize(target, false);
        }
        private Dimension layoutSize(Container target, boolean preferred) {
            synchronized (target.getTreeLock()) {
                int maxWidth  = target.getSize().width;
                if (maxWidth == 0) maxWidth = Integer.MAX_VALUE;
                int nmembers  = target.getComponentCount();
                int x = 0, y = 0, rowH = 0;
                Insets insets = target.getInsets();
                x = insets.left + getHgap();
                for (int i = 0; i < nmembers; i++) {
                    Component m = target.getComponent(i);
                    if (m.isVisible()) {
                        Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();
                        if (x + d.width + getHgap() > maxWidth && x > insets.left + getHgap()) {
                            y += rowH + getVgap(); rowH = 0; x = insets.left + getHgap();
                        }
                        x += d.width + getHgap();
                        rowH = Math.max(rowH, d.height);
                    }
                }
                return new Dimension(maxWidth, y + rowH + getVgap() + insets.bottom);
            }
        }
    }
}