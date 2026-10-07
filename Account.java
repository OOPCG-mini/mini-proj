import java.time.LocalDate;
import java.util.ArrayList;

// One bank account
public class Account {
    public static final double DAILY_LIMIT = 20000;
    public static final double MIN_BALANCE = 500;

    private int accountNumber;
    private String name;
    private String type;          // SAVINGS or CURRENT
    private double balance;
    private ArrayList<Transaction> passbook = new ArrayList<Transaction>();

    public Account(int accountNumber, String name, String type, double openingBalance) {
        this.accountNumber = accountNumber;
        this.name = name;
        this.type = type;
        this.balance = openingBalance;
        passbook.add(new Transaction("OPENING", openingBalance, balance));
    }

    // Returns "OK" if done, otherwise the error message
    public String deposit(double amount) {
        if (amount <= 0) {
            return "Amount must be greater than 0";
        }
        balance = balance + amount;
        passbook.add(new Transaction("DEPOSIT", amount, balance));
        return "OK";
    }

    public String withdraw(double amount) {
        if (amount <= 0) {
            return "Amount must be greater than 0";
        }
        double used = withdrawnToday();
        if (used + amount > DAILY_LIMIT) {
            return "Daily withdrawal limit of " + DAILY_LIMIT + " exceeded. Already withdrawn today: " + used;
        }
        if (balance - amount < MIN_BALANCE) {
            return "Insufficient balance. Minimum balance of " + MIN_BALANCE + " must be maintained";
        }
        balance = balance - amount;
        passbook.add(new Transaction("WITHDRAWAL", amount, balance));
        return "OK";
    }

    // Total money withdrawn today
    public double withdrawnToday() {
        String today = LocalDate.now().toString();
        double total = 0;
        for (int i = 0; i < passbook.size(); i++) {
            Transaction t = passbook.get(i);
            if (t.getType().equals("WITHDRAWAL") && t.getDate().equals(today)) {
                total = total + t.getAmount();
            }
        }
        return total;
    }

    public int getAccountNumber() { return accountNumber; }
    public String getName() { return name; }
    public String getType() { return type; }
    public double getBalance() { return balance; }
    public ArrayList<Transaction> getPassbook() { return passbook; }
}
