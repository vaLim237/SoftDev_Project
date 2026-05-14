package Finals;

/*
 * Shared application context injected into every panel.
 * Holds the single Inventory, TransactionManager, and session info.
 *
 * Also keeps an optional reference to the open InventoryDashboard so that
 * when a kiosk order completes it can push a live refresh to the admin view.
 */
public class AppContext {

    private final Inventory          inventory;
    private final TransactionManager txManager;
    private final String             loggedInUser;
    private final String             role;           // "Barista" or "Manager/Owner"

    /** Set by InventoryDashboard after it opens so the kiosk can call back. */
    private InventoryDashboard dashboard;

    public AppContext(Inventory inventory, TransactionManager txManager,
                      String loggedInUser, String role) {
        this.inventory    = inventory;
        this.txManager    = txManager;
        this.loggedInUser = loggedInUser;
        this.role         = role;
    }

    public Inventory           getInventory()    { return inventory; }
    public TransactionManager  getTxManager()    { return txManager; }
    public String              getLoggedInUser() { return loggedInUser; }
    public String              getRole()         { return role; }

    public boolean isManager() {
        return role.equalsIgnoreCase("Manager/Owner");
    }

    // ── Dashboard live-refresh bridge ─────────────────────────────────────────

    /** Called by InventoryDashboard when it opens. */
    public void registerDashboard(InventoryDashboard d) { this.dashboard = d; }

    /**
     * Called by PaymentPanel / GcashQR after a transaction is confirmed.
     * Refreshes the dashboard's transaction table and inventory table if open.
     */
    public void notifyDashboard() {
        if (dashboard != null) {
            dashboard.refreshFromContext();
        }
    }
}

