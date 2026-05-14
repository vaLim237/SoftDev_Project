package Finals;



import java.awt.*;

/** Centralised colour palette and font helpers used by all UI classes. */
public class Theme {
    public static final Color BG_DARK    = new Color(20, 22, 30);
    public static final Color BG_CARD    = new Color(34, 37, 50);
    public static final Color BG_PANEL   = new Color(28, 30, 42);
    public static final Color ACCENT     = new Color(99, 179, 237);
    public static final Color ACCENT2    = new Color(129, 230, 217);  // teal
    public static final Color DANGER     = new Color(252, 110, 110);
    public static final Color SUCCESS    = new Color(104, 211, 145);
    public static final Color WARNING    = new Color(246, 173, 85);
    public static final Color TEXT_MAIN  = new Color(237, 242, 255);
    public static final Color TEXT_MUTED = new Color(140, 148, 172);
    public static final Color BORDER     = new Color(60, 65, 88);
    public static final Color FIELD_BG   = new Color(46, 50, 68);

    public static Font bold(int size)  { return new Font("Segoe UI", Font.BOLD,  size); }
    public static Font plain(int size) { return new Font("Segoe UI", Font.PLAIN, size); }

    /** Standard accent button */
    public static javax.swing.JButton button(String label) {
        javax.swing.JButton b = new javax.swing.JButton(label);
        b.setBackground(ACCENT);
        b.setForeground(Color.WHITE);
        b.setFont(bold(12));
        b.setFocusPainted(false);
        b.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 18, 8, 18));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    /** Danger (red) button */
    public static javax.swing.JButton dangerButton(String label) {
        javax.swing.JButton b = button(label);
        b.setBackground(DANGER);
        return b;
    }

    /** Success (green) button */
    public static javax.swing.JButton successButton(String label) {
        javax.swing.JButton b = button(label);
        b.setBackground(SUCCESS);
        b.setForeground(new Color(20, 40, 30));
        return b;
    }
}