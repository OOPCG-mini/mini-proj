import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;

// Starts the web server (built into Java, no extra library needed)
public class Main {
    public static Bank bank = new Bank();

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", new MyHandler());
        server.start();
        System.out.println("Server started. Open http://localhost:8080 in your browser");
    }
}
