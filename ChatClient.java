import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.io.*;
import java.net.*;

public class ChatClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private static Scanner sc = new Scanner(System.in);
    private String username;
    private boolean mainControl = true;
    private boolean loggedIn = false;
    private String usernamesInUse = "usernamesInUse.csv";
    private String usernames = "usernames.csv";

    public ChatClient(String username) {
        this.username = username;
    }

    public static void main(String[] args) {
        ChatClient client = new ChatClient("");
        try {
            client.run();
        } catch (IOException e) {
            System.err.println("An error occurred while running the client: " + e.getMessage());
        }
    }

    public void run() throws IOException {
        System.out.println("Welcome to my chat application! ");
        System.out.println("In order to connect to the server for the first time, you must first register with a username.");
        System.out.println("If you have previously connected to the server, you can login and everyone else will receive a message welcoming you back.");

        while (mainControl) {
            System.out.println("Options Menu: ");
            System.out.println("1. Register with username");
            System.out.println("2. Login");
            System.out.println("3. Connect to server and message");
            System.out.println("4. Disconnect from server");
            System.out.print("Enter your option (1,2,3,4): ");

            String input = sc.nextLine();

            int userOption;
            try {
                userOption = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid option. Please enter a number between 1 and 4.");
                continue;
            }

            switch (userOption) {
                case 1:
                    register();
                    break;
                case 2:
                    login();
                    loggedIn = true;
                    break;
                case 3:
                    connectToServer();
                    break;
                case 4:
                    disconnectFromServer();
                    break;
            }
        }
    }

    public void register() {
        System.out.print("Enter your desired username: ");
        String newUsername = sc.nextLine();
        boolean usernameExists = false;

        try {
            Set<String> registeredUsernames = loadUsernamesFromFile(usernames);
            usernameExists = registeredUsernames.contains(newUsername);
        } catch (IOException e) {
            System.err.println("Error reading username file: " + e.getMessage());
        }

        if (newUsername.contains(" ")) {
            System.out.println("Username cannot contain spaces.");
        } else if (usernameExists) {
            System.out.println("Username already taken. Please try another.");
        } else {
            try (PrintWriter printWriter = new PrintWriter(new FileWriter(usernames, true))) {
                printWriter.println(newUsername);
                System.out.println("You have registered successfully!");
                this.username = newUsername;
            } catch (IOException e) {
                System.err.println("An error occurred during registration: " + e.getMessage());
            }
        }
    }

    public void login() {
        System.out.print("What is your username? ");
        String loginUsername = sc.nextLine();
        boolean found = false;
        boolean inUse = false;

        try {
            Set<String> registeredUsernames = loadUsernamesFromFile(usernames);
            found = registeredUsernames.contains(loginUsername);
            Set<String> activeUsernames = loadUsernamesFromFile(usernamesInUse);
            inUse = activeUsernames.contains(loginUsername);
        } catch (IOException e) {
            System.err.println("Error reading username file: " + e.getMessage());
        }

        if (found) {
            if (inUse) {
                System.out.println("The username cannot be in use currently.");
            } else {
                System.out.println("Logged in successfully!");
                loggedIn = true;
            }
        } else {
            System.out.println("Username does not exist. Please register or check your input.");
        }
    }

    // method to add file contents to set
    private static Set<String> loadUsernamesFromFile(String filename) throws IOException {
        Set<String> usernames = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                usernames.add(line.trim());
            }
        }
        return usernames;
    }

    public void connectToServer() throws IOException {
        mainControl = false;
        if (loggedIn && this.username != null) {
            try {
                socket = new Socket("127.0.0.1", 12345);
                System.out.println("Connected to the server!");

                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                out.println(this.username);

                new Thread(() -> {
                    try {
                        String serverMessage;
                        while ((serverMessage = in.readLine()) != null) {
                            System.out.println(serverMessage);
                        }
                    } catch (IOException e) {
                        System.err.println("Connection closed.");
                    }
                }).start();

                try (PrintWriter printWriter = new PrintWriter(new FileWriter(usernamesInUse, true))) {
                    printWriter.println(this.username);
                } catch (IOException e) {
                    System.err.println("An error occurred when adding the username to usernames in use: " + e.getMessage());
                }

                while (true) {
                    System.out.println("Would you like to message the entire server (Enter S) or send direct messages (Enter D), (enter 'exit' to disconnect)? ");
                    String input = sc.nextLine();

                    switch (input) {
                        case "D":
                            dm();
                            break;
                        case "S":
                            serverMessage();
                            break;
                        case "EXIT":
                            System.out.println("Thank you for running my program!");
                            disconnectFromServer();
                            break;
                        default:
                            System.out.println("Invalid input. Please enter S, D, or exit.");
                            break;
                    }
                }

            } catch (Exception e) {
                System.out.println("Unable to connect to the server. Please try again later.");
            }
        } else {
            System.out.println("You must be logged in before connecting with a non-nullable username.");
        }
    }

    public void dm() {
        System.out.println("What is the username of the client you are sending a direct message to? ");
        String directMessageUser = sc.nextLine();

        try {
            Set<String> registeredUsernames = loadUsernamesFromFile(usernames);
            Set<String> activeUsernames = loadUsernamesFromFile(usernamesInUse);

            if (registeredUsernames.contains(directMessageUser)) {
                if (activeUsernames.contains(directMessageUser)) {
                    System.out.println("Username found, in use currently, and connected to client.");
                    while (true) {
                        System.out.println("Enter your message to " + directMessageUser + " (type 'exit' to stop messaging in DMs): ");
                        String directMessage = sc.nextLine();
                        if (directMessage.equalsIgnoreCase("exit")) {
                            break;
                        } else {
                            out.println("(Direct Username): " + directMessageUser);
                            out.println("(Direct Message): " + directMessage);
                        }
                    }
                } else {
                    System.out.println("Username exists but is not in use and connected to the server.");
                }
            } else {
                System.out.println("Username not found.");
            }
        } catch (IOException e) {
            System.err.println("Error loading usernames: " + e.getMessage());
        }
    }

    public void serverMessage() {
        System.out.println("You can now start messaging to the entire server (type 'exit' to stop messaging the entire server): ");
        String userMessage;
        while (true) {
            userMessage = sc.nextLine();
            if (userMessage.equalsIgnoreCase("exit")) {
                break;
            } else {
                out.println("(Broadcast Message): " + userMessage);
            }
        }
    }

    public void disconnectFromServer() {
        if (socket != null) {
            try {
                Set<String> usernamesInUseSet = loadUsernamesFromFile(usernamesInUse);
                usernamesInUseSet.remove(this.username);

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(usernamesInUse))) {
                    for (String activeUsername : usernamesInUseSet) {
                        writer.write(activeUsername);
                        writer.newLine();
                    }
                }
                this.username = null;
                socket.close();
                socket = null;
                System.out.println("Disconnected from server and removed username from file.");
            } catch (IOException e) {
                System.out.println("Error handling disconnection: " + e.getMessage());
            }
        } else {
            System.out.println("You must connected to the server before disconnecting.");
        }
    }
}
