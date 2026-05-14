package Finals;



import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Owner/Manager dashboard.
 * Tabs: Inventory Management | Transaction History | Sales Report
 */
public class InventoryDashboard extends JFrame {

    private static final long serialVersionUID = 1L;

    private final AppContext ctx;

    // Inventory tab
    private DefaultTableModel inventoryModel;
    private JTable            inventoryTable;

    // Transaction tab
    private DefaultTableModel txModel;
    private JTable            txTable;
    private JTextField        txSearch;

    public InventoryDashboard(AppContext ctx) {
        this.ctx = ctx;
        ctx.registerDashboard(this);
        setTitle("ChaiWan Dashboard — " + ctx.getLoggedInUser() + " [" + ctx.getRole() + "]");
        setSize(900, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG_DARK);
        setContentPane(root);

        root.add(buildTopBar(),  BorderLayout.NORTH);
        root.add(buildTabs(),    BorderLayout.CENTER);

        setVisible(true);
    }

    // ── Top bar ───────────────────────────────────────────────────────────────

    private JPanel buildTopBar() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Theme.BG_PANEL);
        p.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        JLabel title = new JLabel("☕ ChaiWan Manager Dashboard");
        title.setFont(Theme.bold(17));
        title.setForeground(Theme.TEXT_MAIN);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setBackground(Theme.BG_PANEL);

        JButton kioskBtn  = Theme.button("Open Kiosk View");
        JButton logoutBtn = Theme.dangerButton("🚪 Logout");

        kioskBtn.addActionListener(e -> new KioskApp(ctx).setVisible(true));
        logoutBtn.addActionListener(e -> { dispose(); new LoginPage(); });

        right.add(kioskBtn);
        right.add(logoutBtn);

        p.add(title, BorderLayout.WEST);
        p.add(right, BorderLayout.EAST);
        return p;
    }

    // ── Tabs ──────────────────────────────────────────────────────────────────

    private JTabbedPane buildTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(Theme.BG_CARD);
        tabs.setForeground(Theme.TEXT_MAIN);
        tabs.setFont(Theme.bold(13));

        tabs.addTab("📦 Inventory",      buildInventoryTab());
        tabs.addTab("📋 Transactions",   buildTransactionTab());
        tabs.addTab("📊 Sales Report",   buildReportTab());
        return tabs;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  INVENTORY TAB
    // ══════════════════════════════════════════════════════════════════════════

    private JPanel buildInventoryTab() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(Theme.BG_DARK);
        p.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // Table
        String[] cols = {"Name", "Category", "Price (₱)", "Stock", "Status"};
        inventoryModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        inventoryTable = styledTable(inventoryModel);
        refreshInventoryTable();

        JScrollPane scroll = new JScrollPane(inventoryTable);
        scroll.setBackground(Theme.BG_CARD);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER, 1));

        // Form panel
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Theme.BG_PANEL);
        form.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER, 1),
            BorderFactory.createEmptyBorder(12, 14, 12, 14)));

        JTextField nameF  = field(); JTextField priceF = field();
        JTextField catF   = field(); JTextField stockF = field();

        String[] catOpts = {"Milk Tea", "Coffee", "Fruit Tea"};
        JComboBox<String> catCombo = new JComboBox<>(catOpts);
        styleCombo(catCombo);

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4, 6, 4, 6);
        gc.fill   = GridBagConstraints.HORIZONTAL;

        addFormRow(form, gc, 0, "Name:",     nameF);
        addFormRow(form, gc, 1, "Category:", catCombo);
        addFormRow(form, gc, 2, "Price (₱):",priceF);
        addFormRow(form, gc, 3, "Stock:",    stockF);

        // Buttons row
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        btns.setBackground(Theme.BG_PANEL);
        JButton addBtn    = Theme.successButton("➕ Add");
        JButton updateBtn = Theme.button("✏ Update");
        JButton removeBtn = Theme.dangerButton("🗑 Remove");
        JButton refreshBtn= Theme.button("🔄 Refresh");
        btns.add(addBtn); btns.add(updateBtn); btns.add(removeBtn); btns.add(refreshBtn);

        // Populate form from selected row
        inventoryTable.getSelectionModel().addListSelectionListener(e -> {
            int row = inventoryTable.getSelectedRow();
            if (row >= 0) {
                nameF.setText((String) inventoryModel.getValueAt(row, 0));
                catCombo.setSelectedItem(inventoryModel.getValueAt(row, 1));
                priceF.setText(String.valueOf(inventoryModel.getValueAt(row, 2)));
                stockF.setText(String.valueOf(inventoryModel.getValueAt(row, 3)));
            }
        });

        addBtn.addActionListener(e -> {
            try {
                String name  = nameF.getText().trim();
                if (name.isEmpty()) throw new Exception("Name cannot be empty");
                double price = Double.parseDouble(priceF.getText().trim());
                String cat   = (String) catCombo.getSelectedItem();
                int    stock = Integer.parseInt(stockF.getText().trim());
                ctx.getInventory().addItem(new FoodItem(name, price, cat, stock));
                refreshInventoryTable();
                clearFields(nameF, priceF, stockF);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage());
            }
        });

        updateBtn.addActionListener(e -> {
            int row = inventoryTable.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Select a row first."); return; }
            try {
                String name  = (String) inventoryModel.getValueAt(row, 0);
                MenuItem item = ctx.getInventory().getItem(name);
                if (item == null) return;
                item.setPrice(Double.parseDouble(priceF.getText().trim()));
                item.setCategory((String) catCombo.getSelectedItem());
                item.setStock(Integer.parseInt(stockF.getText().trim()));
                ctx.getInventory().saveToFile("inventory.csv");
                refreshInventoryTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input.");
            }
        });

        removeBtn.addActionListener(e -> {
            int row = inventoryTable.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Select a row first."); return; }
            String name = (String) inventoryModel.getValueAt(row, 0);
            int ok = JOptionPane.showConfirmDialog(this, "Remove '" + name + "'?",
                "Confirm Remove", JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) {
                ctx.getInventory().removeItem(name);
                refreshInventoryTable();
            }
        });

        refreshBtn.addActionListener(e -> refreshInventoryTable());

        gc.gridy = 4; gc.gridwidth = 2;
        form.add(btns, gc);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scroll, form);
        split.setDividerLocation(300);
        split.setBackground(Theme.BG_DARK);

        p.add(split, BorderLayout.CENTER);
        return p;
    }

    private void refreshInventoryTable() {
        inventoryModel.setRowCount(0);
        for (MenuItem item : ctx.getInventory().getAllItems()) {
            String status = item.getStock() <= 0 ? "Out of Stock"
                          : item.getStock() <= 5  ? "Low Stock"
                          : "OK";
            inventoryModel.addRow(new Object[]{
                item.getName(), item.getCategory(),
                String.format("%.2f", item.getPrice()),
                item.getStock(), status});
        }
    }
    
    public void refreshFromContext() {
        refreshInventoryTable();
        refreshTxTable(ctx.getTxManager().getAll());
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  TRANSACTION HISTORY TAB
    // ══════════════════════════════════════════════════════════════════════════

    private JPanel buildTransactionTab() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(Theme.BG_DARK);
        p.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // Search bar
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchBar.setBackground(Theme.BG_PANEL);
        searchBar.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));

        txSearch = field(); txSearch.setColumns(18);
        JButton searchBtn  = Theme.button("🔍 Search");
        JButton refreshBtn = Theme.button("🔄 Refresh");
        JButton voidBtn    = Theme.dangerButton("🚫 Void Selected");

        searchBar.add(new label("Search: ", Theme.TEXT_MUTED));
        searchBar.add(txSearch);
        searchBar.add(searchBtn);
        searchBar.add(refreshBtn);
        searchBar.add(voidBtn);

        // Table
        String[] cols = {"ID", "Queue#", "Date/Time", "Items", "Subtotal", "Tax", "Discount", "Total", "Payment", "Type", "Status", "By"};
        txModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        txTable = styledTable(txModel);
        txTable.getColumnModel().getColumn(3).setPreferredWidth(180);
        refreshTxTable(ctx.getTxManager().getAll());

        JScrollPane scroll = new JScrollPane(txTable);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER, 1));

        searchBtn.addActionListener(e -> refreshTxTable(
            ctx.getTxManager().search(txSearch.getText().trim())));
        refreshBtn.addActionListener(e -> refreshTxTable(ctx.getTxManager().getAll()));

        voidBtn.addActionListener(e -> {
            int row = txTable.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Select a transaction first."); return; }
            int id = (int) txModel.getValueAt(row, 0);
            String status = (String) txModel.getValueAt(row, 10);
            if ("VOIDED".equals(status)) { JOptionPane.showMessageDialog(this, "Already voided."); return; }

            // Require reason
            String reason = JOptionPane.showInputDialog(this,
                "Enter reason for voiding Transaction #" + id + ":");
            if (reason == null || reason.trim().isEmpty()) return;

            boolean ok = ctx.getTxManager().voidTransaction(id, reason);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Transaction #" + id + " voided.");
                refreshTxTable(ctx.getTxManager().getAll());
            } else {
                JOptionPane.showMessageDialog(this, "Failed to void transaction.");
            }
        });

        p.add(searchBar, BorderLayout.NORTH);
        p.add(scroll,    BorderLayout.CENTER);
        return p;
    }

    private void refreshTxTable(List<Transaction> list) {
        txModel.setRowCount(0);
        for (Transaction t : list) {
            StringBuilder itemsSb = new StringBuilder();
            for (CartItem ci : t.getItems()) {
                if (itemsSb.length() > 0) itemsSb.append(", ");
                itemsSb.append(ci.getQuantity()).append("x ").append(ci.getName());
            }
            txModel.addRow(new Object[]{
                t.getId(), t.getQueueNumber(), t.getFormattedDate(),
                itemsSb.toString(),
                String.format("%.2f", t.getSubtotal()),
                String.format("%.2f", t.getTax()),
                String.format("%.2f", t.getDiscount()),
                String.format("%.2f", t.getTotal()),
                t.getPaymentMethod(), t.getOrderType(),
                t.getStatus().name(), t.getProcessedBy()});
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  SALES REPORT TAB
    // ══════════════════════════════════════════════════════════════════════════

    private JPanel buildReportTab() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(Theme.BG_DARK);
        p.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // Date picker row (use today's date as default)
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        top.setBackground(Theme.BG_PANEL);
        top.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));

        JTextField dateField = field();
        dateField.setColumns(12);
        dateField.setText(LocalDate.now().toString());

        JButton genBtn = Theme.successButton("Generate Report");
        JButton todayBtn = Theme.button("Today");

        top.add(new label("Date (yyyy-MM-dd): ", Theme.TEXT_MUTED));
        top.add(dateField);
        top.add(genBtn);
        top.add(todayBtn);

        JTextArea reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setBackground(Theme.BG_CARD);
        reportArea.setForeground(Theme.TEXT_MAIN);
        reportArea.setFont(new Font("Courier New", Font.PLAIN, 13));
        reportArea.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        JScrollPane scroll = new JScrollPane(reportArea);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER, 1));

        genBtn.addActionListener(e -> {
            try {
                LocalDate date = LocalDate.parse(dateField.getText().trim());
                reportArea.setText(ctx.getTxManager().generateDailyReport(date));
            } catch (Exception ex) {
                reportArea.setText("Invalid date format. Use yyyy-MM-dd.");
            }
        });

        todayBtn.addActionListener(e -> {
            dateField.setText(LocalDate.now().toString());
            reportArea.setText(ctx.getTxManager().generateDailyReport(LocalDate.now()));
        });

        p.add(top,    BorderLayout.NORTH);
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    // ── UI Helpers ────────────────────────────────────────────────────────────

    private JTable styledTable(DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setBackground(Theme.BG_CARD);
        t.setForeground(Theme.TEXT_MAIN);
        t.setSelectionBackground(Theme.ACCENT);
        t.setGridColor(Theme.BORDER);
        t.setFont(Theme.plain(12));
        t.setRowHeight(24);
        t.getTableHeader().setBackground(Theme.BG_PANEL);
        t.getTableHeader().setForeground(Theme.TEXT_MUTED);
        t.getTableHeader().setFont(Theme.bold(12));
        return t;
    }

    private JTextField field() {
        JTextField f = new JTextField();
        f.setBackground(Theme.FIELD_BG);
        f.setForeground(Theme.TEXT_MAIN);
        f.setCaretColor(Theme.ACCENT);
        f.setFont(Theme.plain(13));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER, 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        return f;
    }

    private void styleCombo(JComboBox<String> cb) {
        cb.setBackground(Theme.FIELD_BG);
        cb.setForeground(Theme.TEXT_MAIN);
        cb.setFont(Theme.plain(13));
    }

    private void addFormRow(JPanel form, GridBagConstraints gc, int row,
                            String labelText, JComponent comp) {
        gc.gridy = row;
        gc.gridx = 0; gc.gridwidth = 1; gc.weightx = 0;
        JLabel l = new JLabel(labelText);
        l.setForeground(Theme.TEXT_MUTED);
        l.setFont(Theme.plain(12));
        form.add(l, gc);
        gc.gridx = 1; gc.weightx = 1;
        form.add(comp, gc);
    }

    private void clearFields(JTextField... fields) {
        for (JTextField f : fields) f.setText("");
    }

    /** Tiny anonymous JLabel subclass for inline coloured labels */
    private static class label extends JLabel {
        label(String text, Color color) {
            super(text);
            setForeground(color);
            setFont(Theme.plain(12));
        }
    }
}
