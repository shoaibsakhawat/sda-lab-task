import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class Peer2 {
    private static JTextArea chatArea;
    private static JTextField inputField;
    private static JButton sendButton;
    private static volatile boolean running = true;
    private static final int listenPort = 6000;
    private static final int sendPort = 5000;
    private static final String peerIP = "localhost";
    private static JLabel statusLabel;
    private static ServerSocket serverSocket;
    private static JFrame frame;

    public static void main(String[] args) {
        // Setup GUI
        frame = new JFrame("Peer2 Chat");
        chatArea = new JTextArea(20, 40);
        chatArea.setEditable(false);
        chatArea.setBackground(new Color(245, 255, 250)); // Mint cream
        chatArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(chatArea);
        inputField = new JTextField(30);
        sendButton = new JButton("Send");
        sendButton.setBackground(new Color(100, 149, 237)); // Cornflower blue
        sendButton.setForeground(Color.WHITE);
        JPanel panel = new JPanel();
        panel.setBackground(new Color(230, 230, 250)); // Lavender
        panel.add(inputField);
        panel.add(sendButton);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.getContentPane().add(panel, BorderLayout.SOUTH);
        statusLabel = new JLabel("Peer status: Unknown");
        statusLabel.setOpaque(true);
        statusLabel.setBackground(Color.LIGHT_GRAY);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 13));
        frame.getContentPane().add(statusLabel, BorderLayout.NORTH);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Send message on button click or Enter key
        ActionListener sendAction = e -> sendMessage();
        sendButton.addActionListener(sendAction);
        inputField.addActionListener(sendAction);

        // Thread to listen for incoming messages
        Thread receiveThread = new Thread(() -> {
            try {
                serverSocket = new ServerSocket(listenPort);
                System.out.println("[Peer2 Debug] Waiting for message on port " + listenPort + "...");
                while (running) {
                    try (Socket socket = serverSocket.accept();
                         BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                        String msg = in.readLine();
                        if (msg != null) {
                            appendMessage("Peer1: " + msg);
                            if (msg.equalsIgnoreCase("exit")) {
                                appendMessage("Peer1 has left the chat. You can still send messages.");
                            }
                        }
                    }
                }
            } catch (IOException e) {
                if (running) { // Only print error if not a planned shutdown
                    appendMessage("[Error] " + e.getMessage());
                }
            } finally {
                // Ensure server socket is closed
                try {
                    if (serverSocket != null && !serverSocket.isClosed()) {
                        serverSocket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            appendMessage("Chat ended.");
        });
        receiveThread.start();

        // Add WindowListener to handle GUI close
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                running = false;
                try {
                    if (serverSocket != null && !serverSocket.isClosed()) {
                        serverSocket.close(); // Close the server socket gracefully
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                // System.exit(0); // Default close operation usually handles this
            }
        });
    }

    private static void sendMessage() {
        String msg = inputField.getText().trim();
        if (msg.isEmpty() || !running) return;
        appendMessage("You: " + msg);
        inputField.setText("");
        boolean sent = false;
        int attempts = 0;
        while (!sent && attempts < 3 && running) {
            try (Socket socket = new Socket(peerIP, sendPort);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                out.println(msg);
                sent = true;
            } catch (IOException e) {
                attempts++;
                appendMessage("[Send Error] Peer not available (attempt " + attempts + "). Retrying...");
                setStatus(false);
                try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
            }
        }
        if (!sent) {
            appendMessage("[Send Error] Could not connect to peer after 3 attempts.");
            setStatus(false);
        } else {
            setStatus(true);
        }
        if (msg.equalsIgnoreCase("exit")) {
            running = false;
            frame.dispose(); // Close the GUI window
        }
    }

    private static void appendMessage(String msg) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append(msg + "\n");
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        });
    }

    private static void setStatus(boolean available) {
        SwingUtilities.invokeLater(() -> {
            if (available) {
                statusLabel.setText("Peer status: Available");
                statusLabel.setBackground(new Color(144, 238, 144)); // Light green
            } else {
                statusLabel.setText("Peer status: Not available");
                statusLabel.setBackground(new Color(255, 99, 71)); // Tomato red
            }
        });
    }
} 