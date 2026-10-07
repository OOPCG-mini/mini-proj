import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

// Handles every request coming from the website
public class MyHandler implements HttpHandler {

    public void handle(HttpExchange ex) throws IOException {
        String path = ex.getRequestURI().getPath();
        HashMap<String, String> p = readParams(ex.getRequestURI().getRawQuery());
        String response;
        String contentType = "application/json";

        try {
            if (path.equals("/") || path.equals("/index.html")) {
                response = new String(Files.readAllBytes(Paths.get("index.html")), "UTF-8");
                contentType = "text/html";
            } else if (path.equals("/style.css")) {
                response = new String(Files.readAllBytes(Paths.get("style.css")), "UTF-8");
                contentType = "text/css";
            } else if (path.equals("/api/create")) {
                response = create(p);
            } else if (path.equals("/api/deposit")) {
                response = deposit(p);
            } else if (path.equals("/api/withdraw")) {
                response = withdraw(p);
            } else if (path.equals("/api/balance")) {
                response = balance(p);
            } else if (path.equals("/api/info")) {
                response = info(p);
            } else if (path.equals("/api/passbook")) {
                response = passbook(p);
            } else {
                response = message(false, "Unknown request");
            }
        } catch (Exception e) {
            response = message(false, "Invalid input. Please check the values entered");
        }

        byte[] bytes = response.getBytes("UTF-8");
        ex.getResponseHeaders().add("Content-Type", contentType + "; charset=UTF-8");
        ex.sendResponseHeaders(200, bytes.length);
        OutputStream os = ex.getResponseBody();
        os.write(bytes);
        os.close();
    }

    // ---------- the 6 operations ----------

    private String create(HashMap<String, String> p) {
        String name = p.get("name");
        String type = p.get("type");
        double opening = Double.parseDouble(p.get("balance"));
        Account a = Main.bank.createAccount(name, type, opening);
        if (a == null) {
            return message(false, "Name is required and opening balance must be at least " + Account.MIN_BALANCE);
        }
        return message(true, "Account created. Your account number is " + a.getAccountNumber());
    }

    private String deposit(HashMap<String, String> p) {
        Account a = Main.bank.findAccount(Integer.parseInt(p.get("acc")));
        if (a == null) return message(false, "Account not found");
        String result = a.deposit(Double.parseDouble(p.get("amount")));
        if (result.equals("OK")) {
            return message(true, "Deposit successful. New balance: " + a.getBalance());
        }
        return message(false, result);
    }

    private String withdraw(HashMap<String, String> p) {
        Account a = Main.bank.findAccount(Integer.parseInt(p.get("acc")));
        if (a == null) return message(false, "Account not found");
        String result = a.withdraw(Double.parseDouble(p.get("amount")));
        if (result.equals("OK")) {
            return message(true, "Withdrawal successful. New balance: " + a.getBalance());
        }
        return message(false, result);
    }

    private String balance(HashMap<String, String> p) {
        Account a = Main.bank.findAccount(Integer.parseInt(p.get("acc")));
        if (a == null) return message(false, "Account not found");
        return message(true, "Current balance: " + a.getBalance());
    }

    private String info(HashMap<String, String> p) {
        Account a = Main.bank.findAccount(Integer.parseInt(p.get("acc")));
        if (a == null) return message(false, "Account not found");
        return "{\"ok\":true,"
            + "\"accountNumber\":" + a.getAccountNumber() + ","
            + "\"name\":\"" + clean(a.getName()) + "\","
            + "\"type\":\"" + a.getType() + "\","
            + "\"balance\":" + a.getBalance() + ","
            + "\"dailyLimit\":" + Account.DAILY_LIMIT + ","
            + "\"withdrawnToday\":" + a.withdrawnToday() + "}";
    }

    private String passbook(HashMap<String, String> p) {
        Account a = Main.bank.findAccount(Integer.parseInt(p.get("acc")));
        if (a == null) return message(false, "Account not found");
        String from = p.get("from");   // dates look like 2026-10-07, so
        String to = p.get("to");       // plain String comparison works
        if (from == null || to == null || from.equals("") || to.equals("")) {
            return message(false, "Please select both From and To dates");
        }
        ArrayList<Transaction> list = a.getPassbook();
        String rows = "";
        for (int i = 0; i < list.size(); i++) {
            Transaction t = list.get(i);
            if (t.getDate().compareTo(from) >= 0 && t.getDate().compareTo(to) <= 0) {
                if (!rows.equals("")) rows = rows + ",";
                rows = rows + "{\"id\":" + t.getId()
                    + ",\"date\":\"" + t.getDate() + "\""
                    + ",\"type\":\"" + t.getType() + "\""
                    + ",\"amount\":" + t.getAmount()
                    + ",\"balance\":" + t.getBalanceAfter() + "}";
            }
        }
        return "{\"ok\":true,\"name\":\"" + clean(a.getName()) + "\",\"balance\":" + a.getBalance()
            + ",\"transactions\":[" + rows + "]}";
    }

    // ---------- small helper methods ----------

    private String message(boolean ok, String text) {
        return "{\"ok\":" + ok + ",\"message\":\"" + clean(text) + "\"}";
    }

    private String clean(String s) {
        return s.replace("\\", "").replace("\"", "'");
    }

    // turns "acc=10001&amount=500" into a HashMap
    private HashMap<String, String> readParams(String query) {
        HashMap<String, String> map = new HashMap<String, String>();
        if (query == null) return map;
        String[] pairs = query.split("&");
        for (int i = 0; i < pairs.length; i++) {
            String[] kv = pairs[i].split("=", 2);
            if (kv.length == 2) {
                try {
                    map.put(kv[0], URLDecoder.decode(kv[1], "UTF-8"));
                } catch (Exception e) {
                    // ignore bad value
                }
            }
        }
        return map;
    }
}