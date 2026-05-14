package Finals;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages transaction history, queue numbers, and sales reporting.
 * Persists transactions to transactions.csv.
 */
public class TransactionManager {

    private static final String FILE      = "transactions.csv";
    private static final double TAX_RATE  = 0.12;   // 12% VAT

    private List<Transaction> history = new ArrayList<>();
    private int queueCounter = 1;

    public TransactionManager() {
        loadFromFile();
        // set queue counter past highest existing queue number
        history.stream()
               .mapToInt(Transaction::getQueueNumber)
               .max()
               .ifPresent(max -> queueCounter = max + 1);
    }

    // ── Queue ────────────────────────────────────────────────────────────────

    public int nextQueueNumber() { return queueCounter++; }

    // ── Create transaction ───────────────────────────────────────────────────

    /**
     * Builds a completed transaction and appends it to history.
     *
     * @param items          cart items
     * @param discountAmount flat discount amount (₱)
     * @param paymentMethod  Cash / GCash / Maya
     * @param orderType      Dine In / Take Out
     * @param processedBy    logged-in username
     * @return the new Transaction
     */
    public Transaction createTransaction(List<CartItem> items,
                                         double discountAmount,
                                         String paymentMethod,
                                         String orderType,
                                         String processedBy) {
        double subtotal = items.stream().mapToDouble(CartItem::getTotalPrice).sum();
        double tax      = subtotal * TAX_RATE;
        int queueNum    = nextQueueNumber();

        Transaction t = new Transaction(queueNum, items, subtotal, tax,
                                        discountAmount, paymentMethod,
                                        orderType, processedBy);
        history.add(t);
        appendToFile(t);
        return t;
    }

    // ── Void ─────────────────────────────────────────────────────────────────

    public boolean voidTransaction(int transactionId, String reason) {
        Optional<Transaction> opt = history.stream()
                .filter(t -> t.getId() == transactionId)
                .findFirst();
        if (opt.isPresent() && !opt.get().isVoided()) {
            opt.get().voidTransaction(reason);
            rewriteFile();
            return true;
        }
        return false;
    }

    // ── Search / Filter ──────────────────────────────────────────────────────

    public List<Transaction> getAll()    { return Collections.unmodifiableList(history); }

    public List<Transaction> search(String keyword) {
        String kw = keyword.toLowerCase();
        return history.stream()
                .filter(t -> t.getFormattedDate().contains(kw)
                          || t.getPaymentMethod().toLowerCase().contains(kw)
                          || t.getOrderType().toLowerCase().contains(kw)
                          || String.valueOf(t.getId()).contains(kw)
                          || String.valueOf(t.getQueueNumber()).contains(kw)
                          || t.getStatus().name().toLowerCase().contains(kw))
                .collect(Collectors.toList());
    }

    public List<Transaction> getByDate(LocalDate date) {
        return history.stream()
                .filter(t -> t.getTimestamp().toLocalDate().equals(date))
                .collect(Collectors.toList());
    }

    // ── Sales Report ─────────────────────────────────────────────────────────

    /** Summarises all non-voided transactions for a given date */
    public String generateDailyReport(LocalDate date) {
        List<Transaction> daily = getByDate(date).stream()
                .filter(t -> !t.isVoided())
                .collect(Collectors.toList());

        if (daily.isEmpty())
            return "No transactions found for " + date + ".";

        double totalSales    = daily.stream().mapToDouble(Transaction::getTotal).sum();
        double totalTax      = daily.stream().mapToDouble(Transaction::getTax).sum();
        double totalDiscount = daily.stream().mapToDouble(Transaction::getDiscount).sum();

        // best-sellers
        Map<String, Integer> itemCount = new LinkedHashMap<>();
        for (Transaction t : daily)
            for (CartItem ci : t.getItems())
                itemCount.merge(ci.getName(), ci.getQuantity(), Integer::sum);

        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════\n");
        sb.append("  DAILY SALES REPORT — ").append(date).append("\n");
        sb.append("═══════════════════════════════════════\n");
        sb.append(String.format("  Transactions : %d%n", daily.size()));
        sb.append(String.format("  Subtotal     : ₱%.2f%n",
                  daily.stream().mapToDouble(Transaction::getSubtotal).sum()));
        sb.append(String.format("  Tax (12%%)    : ₱%.2f%n", totalTax));
        sb.append(String.format("  Discounts    : -₱%.2f%n", totalDiscount));
        sb.append(String.format("  TOTAL SALES  : ₱%.2f%n", totalSales));
        sb.append("───────────────────────────────────────\n");
        sb.append("  Payment Breakdown:\n");
        Map<String, Long> byPayment = daily.stream()
                .collect(Collectors.groupingBy(Transaction::getPaymentMethod,
                         Collectors.counting()));
        byPayment.forEach((k, v) -> sb.append(String.format("    %-10s: %d orders%n", k, v)));
        sb.append("───────────────────────────────────────\n");
        sb.append("  Best Sellers:\n");
        itemCount.entrySet().stream()
                 .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                 .limit(5)
                 .forEach(e -> sb.append(String.format("    %-25s x%d%n", e.getKey(), e.getValue())));
        sb.append("═══════════════════════════════════════\n");
        return sb.toString();
    }

    // ── Persistence ──────────────────────────────────────────────────────────

    private void appendToFile(Transaction t) {
        try (PrintWriter out = new PrintWriter(new FileWriter(FILE, true))) {
            out.println(buildCsvLine(t));
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void rewriteFile() {
        try (PrintWriter out = new PrintWriter(new FileWriter(FILE, false))) {
            for (Transaction t : history) out.println(buildCsvLine(t));
        } catch (IOException e) { e.printStackTrace(); }
    }

    private String buildCsvLine(Transaction t) {
        // id,queueNum,date,subtotal,tax,discount,total,payment,orderType,status,voidReason,processedBy,items
        StringBuilder items = new StringBuilder();
        for (CartItem ci : t.getItems()) {
            if (items.length() > 0) items.append("|");
            items.append(ci.getName()).append(";")
                 .append(ci.getQuantity()).append(";")
                 .append(ci.getUnitPrice()).append(";")
                 .append(ci.getSugarLevel());
        }
        return t.getId() + "," +
               t.getQueueNumber() + "," +
               t.getFormattedDate() + "," +
               t.getSubtotal() + "," +
               t.getTax() + "," +
               t.getDiscount() + "," +
               t.getTotal() + "," +
               t.getPaymentMethod() + "," +
               t.getOrderType() + "," +
               t.getStatus() + "," +
               (t.getVoidReason() != null ? t.getVoidReason().replace(",", ";") : "") + "," +
               t.getProcessedBy() + "," +
               items;
    }

    private void loadFromFile() {
        history.clear();
        // CSV parsing for display purposes; full reconstruction is complex
        // so we only load a lightweight view for history display
        // (full state is kept in memory during a session)
        File f = new File(FILE);
        if (!f.exists()) return;
        // We skip re-hydrating full objects; transactions are memory-only per session.
        // The CSV file acts as an audit log.
    }

    /** How many non-voided transactions exist */
    public int getCompletedCount() {
        return (int) history.stream().filter(t -> !t.isVoided()).count();
    }

    public static double getTaxRate() { return TAX_RATE; }
}