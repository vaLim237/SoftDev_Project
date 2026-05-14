package Finals;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

public class LoginPage extends JFrame {

    private static final long serialVersionUID = 1L;

    // ── Account store: email → {password, role[]} ────────────────────────────
    private static final Map<String, Object[]> ACCOUNTS = new HashMap<>();
    static {
        ACCOUNTS.put("admin@usc.edu.ph",    new Object[]{"dreamteam101", new String[]{"Manager/Owner"}});
        ACCOUNTS.put("manager@usc.edu.ph",  new Object[]{"dreamteam101", new String[]{"Manager/Owner"}});
        ACCOUNTS.put("barista@usc.edu.ph",  new Object[]{"dreamteam101", new String[]{"Barista"}});
        ACCOUNTS.put("denzel@usc.edu.ph",   new Object[]{"dreamteam101", new String[]{"Manager/Owner"}});
        ACCOUNTS.put("valerie@usc.edu.ph",  new Object[]{"dreamteam101", new String[]{"Barista"}});
        ACCOUNTS.put("dreamteam@usc.edu.ph",new Object[]{"dreamteam101", new String[]{"Manager/Owner"}});
    }

    private JTextField   usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleCombo;
    private JLabel       statusLabel;
    private JButton      loginButton;

    private static final String USER_PH = "e.g. barista@usc.edu.ph";
    private static final String PASS_PH = "Enter password";

    public LoginPage() {
        setTitle("ChaiWan POS — Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(440, 560);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(Theme.BG_DARK);
        setContentPane(root);
        root.add(buildCard());
        setVisible(true);
    }

    private JPanel buildCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Theme.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER, 1),
            BorderFactory.createEmptyBorder(32, 44, 32, 44)));

        JLabel logo = new JLabel("☕");
        logo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 46));
        logo.setAlignmentX(CENTER_ALIGNMENT);

        JLabel title = new JLabel("ChaiWan POS");
        title.setFont(Theme.bold(22));
        title.setForeground(Theme.TEXT_MAIN);
        title.setAlignmentX(CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Cafe Ingredients Inventory");
        sub.setFont(Theme.plain(12));
        sub.setForeground(Theme.TEXT_MUTED);
        sub.setAlignmentX(CENTER_ALIGNMENT);

        usernameField  = new JTextField();
        passwordField  = new JPasswordField();
        styleField(usernameField, USER_PH);
        styleField(passwordField, PASS_PH);

        String[] roles = {"Barista", "Manager/Owner"};
        roleCombo = new JComboBox<>(roles);
        styleCombo(roleCombo);

        statusLabel = new JLabel(" ");
        statusLabel.setFont(Theme.plain(12));
        statusLabel.setAlignmentX(CENTER_ALIGNMENT);

        loginButton = Theme.button("Log In");
        loginButton.setAlignmentX(CENTER_ALIGNMENT);
        loginButton.addActionListener(e -> handleLogin());

        card.add(logo);
        card.add(Box.createVerticalStrut(4));
        card.add(title);
        card.add(sub);
        card.add(Box.createVerticalStrut(24));
        card.add(makeLabel("Select Role"));
        card.add(Box.createVerticalStrut(4));
        card.add(roleCombo);
        card.add(Box.createVerticalStrut(14));
        card.add(makeLabel("Username"));
        card.add(Box.createVerticalStrut(4));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(14));
        card.add(makeLabel("Password"));
        card.add(Box.createVerticalStrut(4));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(18));
        card.add(statusLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(loginButton);
        return card;
    }

    private void handleLogin() {
        String user   = usernameField.getText().trim();
        String pass   = new String(passwordField.getPassword());
        String role   = (String) roleCombo.getSelectedItem();

        if (user.equals(USER_PH) || user.isEmpty()) { showError("Please enter your username."); return; }
        if (!user.toLowerCase().endsWith("@usc.edu.ph")) { showError("Use @usc.edu.ph email only."); return; }
        if (!ACCOUNTS.containsKey(user.toLowerCase())) { showError("Account not found."); return; }

        Object[] creds = ACCOUNTS.get(user.toLowerCase());
        if (!pass.equals(creds[0])) { showError("Incorrect password."); return; }

        String[] allowedRoles = (String[]) creds[1];
        boolean match = false;
        for (String r : allowedRoles) if (r.equalsIgnoreCase(role)) { match = true; break; }
        if (!match) { showError("You don't have the '" + role + "' role."); return; }

        showSuccess("Login successful! Loading…");
        loginButton.setEnabled(false);
        Timer t = new Timer(900, e -> {
            ((Timer) e.getSource()).stop();
            dispose();
            launchApp(user, role);
        });
        t.setRepeats(false);
        t.start();
    }

    private void launchApp(String user, String role) {
        Inventory          inv = new Inventory();
        TransactionManager tm  = new TransactionManager();
        AppContext          ctx = new AppContext(inv, tm, user, role);

        if (role.equalsIgnoreCase("Barista")) {
            new KioskApp(ctx).setVisible(true);
        } else {
            new InventoryDashboard(ctx).setVisible(true);
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void styleField(JTextField f, String ph) {
        f.setText(ph);
        f.setForeground(Theme.TEXT_MUTED);
        f.setBackground(Theme.FIELD_BG);
        f.setCaretColor(Theme.ACCENT);
        f.setFont(Theme.plain(13));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        if (f instanceof JPasswordField) ((JPasswordField) f).setEchoChar((char) 0);
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (f.getText().equals(ph)) {
                    f.setText(""); f.setForeground(Theme.TEXT_MAIN);
                    if (f instanceof JPasswordField) ((JPasswordField) f).setEchoChar('●');
                }
            }
            public void focusLost(FocusEvent e) {
                if (f.getText().isEmpty()) {
                    f.setText(ph); f.setForeground(Theme.TEXT_MUTED);
                    if (f instanceof JPasswordField) ((JPasswordField) f).setEchoChar((char) 0);
                }
            }
        });
    }

    private void styleCombo(JComboBox<String> cb) {
        cb.setBackground(Theme.FIELD_BG);
        cb.setForeground(Theme.TEXT_MAIN);
        cb.setFont(Theme.plain(13));
        cb.setAlignmentX(LEFT_ALIGNMENT);
    }

    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(Theme.TEXT_MUTED);
        l.setFont(Theme.bold(11));
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private void showError(String m) { statusLabel.setText(m); statusLabel.setForeground(Theme.DANGER); }
    private void showSuccess(String m) { statusLabel.setText(m); statusLabel.setForeground(Theme.SUCCESS); }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginPage::new);
    }
}