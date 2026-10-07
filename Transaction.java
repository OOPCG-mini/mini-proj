import java.time.LocalDate;

// One line of the passbook
public class Transaction {
    private static int counter = 1000;

    private int id;
    private String date;      // like 2026-10-07
    private String type;      // OPENING, DEPOSIT or WITHDRAWAL
    private double amount;
    private double balanceAfter;

    public Transaction(String type, double amount, double balanceAfter) {
        counter++;
        this.id = counter;
        this.date = LocalDate.now().toString();
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
    }

    public int getId() { return id; }
    public String getDate() { return date; }
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public double getBalanceAfter() { return balanceAfter; }
}
