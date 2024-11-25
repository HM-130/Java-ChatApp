# Java-ChatApp

**Completed at age 14 - November 2024**

---

## Overview

This application is a simple yet feature-rich chat application built in Java. It demonstrates core concepts of **socket programming**, **multithreading**, and **basic networking**. This project includes both client and server implementations, enabling real-time communication through group and direct messaging.

Key features:
- **Client-Server Architecture**: Server manages all client communications.
- **User Registration and Login**: Ensures a unique identity for every user.
- **Broadcast and Direct Messaging**: Allows users to send messages to everyone or specific individuals.

---

## Features

1. **User Registration and Login**
   - New users can register a unique username.
   - Existing users can log in and are notified when rejoining the server.

2. **Messaging Options**
   - **Broadcast Messages**: Send messages visible to all connected clients.
   - **Direct Messages**: Send private messages to specific users online.

3. **Active User Management**
   - Tracks active users to prevent username duplication during login.

4. **Dynamic Disconnection Handling**
   - Automatically removes disconnected users from the active user list.

---

## Project Files

### Server-Side (`ChatServer.java`)
- Handles client connections using a **multithreaded architecture**.
- Broadcasts messages or relays direct messages to specified recipients.
- Maintains a list of active users for efficient message delivery.

### Client-Side (`ChatClient.java`)
- Provides an interactive CLI for user registration, login, and messaging.
- Supports switching between broadcast and direct messaging seamlessly.
- Dynamically handles disconnection and reconnection scenarios.

---

## Technology Stack

- **Programming Language**: Java
- **Core Concepts Used**:
  - Socket Programming (TCP/IP)
  - Multithreading
  - File I/O for username persistence

---

## How to Run

### Prerequisites

- **Java Development Kit (JDK)**: Version 8 or later.
- **Java Runtime Environment (JRE)**: To execute the compiled program.

### Steps

1. **Run the Server**
   - Compile and start the server:
     ```bash
     javac ChatServer.java
     java ChatServer
     ```
   - The server listens on port `12345` by default.

2. **Run the Client**
   - Compile and start the client:
     ```bash
     javac ChatClient.java
     java ChatClient
     ```

3. **Client Interaction**
   - Register a unique username.
   - Log in to an existing username.
   - Connect to the server and start messaging.

---

## Usage Instructions

### Command-Line Interface: Client Options

1. **Register**: Create a new username.
2. **Login**: Log in using an existing username.
3. **Messaging**:
   - **Broadcast**: Send a message to all users with the `S` option.
   - **Direct Message**: Send a private message with the `D` option.
4. **Disconnect**: Leave the chat and free up the username for others.

---

## Potential Improvements

- **Graphical User Interface (GUI)**: Replace the CLI with a user-friendly interface using JavaFX or Swing.
- **Encryption**: Secure messages with SSL/TLS for privacy.
- **Message History**: Add a database to log chat history for later retrieval.
- **File Sharing**: Extend functionality to support file transfers between users.
- **Server Scaling**: Use load balancing to support more concurrent users.

---

## Acknowledgements

This project was developed as a self-learning initiative to explore networking and multithreading concepts in Java. It demonstrates how a functional, multi-client chat application can be built from scratch.
