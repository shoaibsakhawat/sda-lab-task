import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Peer {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your listening port: ");
        int listenPort = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter peer IP (e.g., localhost): ");
        String peerIP = scanner.nextLine();
        System.out.print("Enter peer port: ");
        int peerPort = Integer.parseInt(scanner.nextLine());

        // Thread to listen for incoming messages
        Thread receiveThread = new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(listenPort)) {
                while (true) {
                    try (Socket socket = serverSocket.accept();
                         BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                        String msg = in.readLine();
                        if (msg != null) {
                            System.out.println("Peer: " + msg);
                            if (msg.equalsIgnoreCase("exit")) break;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        receiveThread.start();

        // Main thread: send messages
        try (BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {
            String msg;
            while (true) {
                System.out.print("You: ");
                msg = userInput.readLine();
                try (Socket socket = new Socket(peerIP, peerPort);
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                    out.println(msg);
                }
                if (msg.equalsIgnoreCase("exit")) break;
    }
}
        // Wait for receive thread to finish
        try { receiveThread.join(); } catch (InterruptedException ignored) {}
        System.out.println("Chat ended.");
    }
}