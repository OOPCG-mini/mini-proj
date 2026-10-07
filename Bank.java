import java.util.ArrayList;

// Keeps the list of all accounts
public class Bank {
    private ArrayList<Account> accounts = new ArrayList<Account>();
    private int nextNumber = 10001;

    // returns null if the details are not valid
    public Account createAccount(String name, String type, double openingBalance) {
        if (name == null || name.trim().equals("")) {
            return null;
        }
        if (openingBalance < Account.MIN_BALANCE) {
            return null;
        }
        Account a = new Account(nextNumber, name.trim(), type, openingBalance);
        nextNumber++;
        accounts.add(a);
        return a;
    }

    // returns null if account is not found
    public Account findAccount(int number) {
        for (int i = 0; i < accounts.size(); i++) {
            if (accounts.get(i).getAccountNumber() == number) {
                return accounts.get(i);
            }
        }
        return null;
    }
}
