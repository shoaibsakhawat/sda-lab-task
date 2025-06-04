import java.io.*;
import java.net.*;

public class Peer {
    private static final int PORT = 5000;

    public static void main(String[] args) {
        try {
            // Start server socket to listen for incoming messages
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Peer is listening on port " + PORT);

            // Server thread to accept incoming messages
            Thread serverThread = new Thread(() -> {
                while (true) {
                    try (Socket clientSocket = serverSocket.accept();
                         BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                        String message = in.readLine();
                        System.out.println("Received: " + message);
                    } catch (IOException e) {
                        System.err.println("Error receiving message: " + e.getMessage());
                    }
                }
            });
            serverThread.start();

            // Give the server a moment to start
            Thread.sleep(1000);

            // Send a message to the local peer (yourself)
            try (Socket socket = new Socket("localhost", PORT);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                out.println("Hello from peer!");
            } catch (IOException e) {
                System.err.println("Error sending message: " + e.getMessage());
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Startup error: " + e.getMessage());
        }
    }
}
