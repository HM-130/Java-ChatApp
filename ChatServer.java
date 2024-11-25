import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.io.*;
import java.net.*;

public class ChatServer {
    private static List<PrintWriter> clientWriters = new CopyOnWriteArrayList<>(); // List of client print writers
    private static ServerSocket serverSocket;
    private static Map<String, PrintWriter> userWriters = new HashMap<>(); // Map to hold username and writer

    public static void main(String[] args) throws IOException {
        System.out.println("Server has been started...");
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Server listening at port 12345...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                new Thread(clientHandler).start();
            }
        }
    }

    // Getter for user writers (used for direct messaging)
    public static Map<String, PrintWriter> getUserWriters() {
        return userWriters;
    }

    // Method to broadcast messages to all connected clients
    public static void broadcastMessage(String message) {
        for (PrintWriter writer : clientWriters) {
            writer.println(message);
        }
    }

    static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private PrintWriter out;
        private String username;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                clientWriters.add(out);

                // Read the username from the client and store it in the userWriters map
                username = in.readLine();
                System.out.println("A new user has connected: " + username);
                out.println("Welcome " + username + "!"); // Send welcome message to the client
                userWriters.put(username, out);

                String message;
                while ((message = in.readLine()) != null) {
                    if (message.contains("(Direct Username): ")) {
                        // Handle direct message
                        String directMessageUser = message.replace("(Direct Username): ", "");
                        String dmMessage = in.readLine().replace("(Direct Message): ", "");
                        PrintWriter privateOut = userWriters.get(directMessageUser);

                        if (privateOut != null) {
                            privateOut.println("Direct message from " + username + ": " + dmMessage);
                        }
                    } else if (message.contains("(Broadcast Message): ")) {
                        // Handle broadcast message
                        message = message.replace("(Broadcast Message): ", "");
                        broadcastMessage("Server message from " + username + ": " + message);
                    } else {
                        System.out.println("An error has occurred.");
                    }
                }
                
            } catch (IOException e) {
                System.err.println("Connection closed for " + username);
            } finally {
                // Cleanup after disconnection
                try {
                    if (out != null) {
                        clientWriters.remove(out);
                    }
                    clientSocket.close();
                    System.out.println(username + " has disconnected.");
                    userWriters.remove(username);
                } catch (IOException e) {
                    System.err.println("Error closing client socket for " + username);
                }
            }
        }

        public void stopServer() throws IOException{
            System.out.println("Stopping server");
            serverSocket.close();
        }
    }
}
