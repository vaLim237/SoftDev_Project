package Finals;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

public class Transaction {
    public enum Status { COMPLETED, VOIDED }

    private static int counter = 1;

    private int id;
    private int queueNumber;
    private List<CartItem> items;
    private double subtotal;
    private double tax;         // amount (not rate)
    private double discount;    // amount
    private double total;
    private String paymentMethod;
    private String orderType;   // Dine In / Take Out
    private LocalDateTime timestamp;
    private Status status;
    private String voidReason;
    private String processedBy; // username

    public Transaction(int queueNumber, List<CartItem> items,
                       double subtotal, double tax, double discount,
                       String paymentMethod, String orderType, String processedBy) {
        this.id            = counter++;
        this.queueNumber   = queueNumber;
        this.items         = new ArrayList<>(items);
        this.subtotal      = subtotal;
        this.tax           = tax;
        this.discount      = discount;
        this.total         = subtotal + tax - discount;
        this.paymentMethod = paymentMethod;
        this.orderType     = orderType;
        this.timestamp     = LocalDateTime.now();
        this.status        = Status.COMPLETED;
        this.processedBy   = processedBy;
    }

    // ── getters ──────────────────────────────────────────────────────────────
    public int getId()               { return id; }
    public int getQueueNumber()      { return queueNumber; }
    public List<CartItem> getItems() { return items; }
    public double getSubtotal()      { return subtotal; }
    public double getTax()           { return tax; }
    public double getDiscount()      { return discount; }
    public double getTotal()         { return total; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getOrderType()     { return orderType; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public Status getStatus()        { return status; }
    public String getVoidReason()    { return voidReason; }
    public String getProcessedBy()   { return processedBy; }

    // ── void ─────────────────────────────────────────────────────────────────
    public void voidTransaction(String reason) {
        this.status     = Status.VOIDED;
        this.voidReason = reason;
    }

    public boolean isVoided() { return status == Status.VOIDED; }

    public String getFormattedDate() {
        return timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    @Override
    public String toString() {
        return String.format("[#%d | Q%d | %s | ₱%.2f | %s | %s]",
            id, queueNumber, getFormattedDate(), total, paymentMethod,
            status == Status.VOIDED ? "VOIDED" : "OK");
    }

    /** Reset counter (call at app start or for testing) */
    public static void resetCounter(int start) { counter = start; }
}
