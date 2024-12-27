package client;

import Characters.*;
import Characters.Character;
import graphicScenes.ActionsPanel;
import graphicScenes.MapGenerator;
import server.GameServer;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.net.InetSocketAddress;
import java.util.Random;

/**
 * The GameClient class handles interaction with the game server, including establishing a connection,
 * managing game updates, and responding to server communications. It initializes the player's hero
 * character, creates the game GUI, and processes ongoing updates from the server.
 *
 * This class is responsible for:
 * - Connecting to the server via a SocketChannel.
 * - Sending and receiving data using DataInputStream and DataOutputStream.
 * - Handling player registration and initialization of the hero.
 * - Creating the game GUI and rendering components.
 * - Listening for server updates to handle hero and enemy position changes.
 * - Managing connection loss and automatic reconnection to the server.
 */
public class GameClient {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;
    private static final int BUFFER_SIZE = 1024;

    public void startGame(String username) {
        final SocketChannel socketChannel;
        final DataOutputStream out;
        final DataInputStream in;

        try {
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(true);
            socketChannel.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT));

            out = new DataOutputStream(socketChannel.socket().getOutputStream());
            in = new DataInputStream(socketChannel.socket().getInputStream());

            // Send the username to the server
            out.writeUTF(username);
            out.flush();
            System.out.println("Sent username to server: " + username);

            // Handle the server's registration response
            String response = in.readUTF();
            System.out.println("Server response: " + response);

            if (response.startsWith("REGISTERED:HERO_AT(")) {
                System.out.println("Processing registration response...");

                // Extract hero's position
                String coordinates = response.substring("REGISTERED:HERO_AT(".length(), response.length() - 1);
                String[] xy = coordinates.split(",");
                int x = Integer.parseInt(xy[0].trim());
                int y = Integer.parseInt(xy[1].trim());

                // Create Hero object
                Hero hero = new Hero(username, "DefaultHero", new Position(x, y), "images/mainChar2.png");
                System.out.println("Hero created: " + hero);

                // Initialize MapGenerator with Hero
                MapGenerator mapGenerator = new MapGenerator();
                mapGenerator.setHero(hero);

                // Call GUI creation on the event dispatch thread
                SwingUtilities.invokeLater(() -> {
                    try {
                        createAndShowGUI(socketChannel, in, out, mapGenerator);
                        System.out.println("GUI initialized successfully.");
                    } catch (IOException e) {
                        System.err.println("Error during GUI creation: " + e.getMessage());
                        e.printStackTrace();
                    }
                });
            } else {
                System.err.println("Unexpected server response: " + response);
            }

        } catch (IOException e) {
            System.err.println("Failed to connect to the server: " + e.getMessage());
        }
    }
    private boolean isConnectionValid(SocketChannel channel) {
        return channel != null && channel.isOpen() && channel.isConnected();
    }

    private void handleConnectionLoss(SocketChannel oldSocketChannel, MapGenerator mapGenerator) {
        System.err.println("Connection lost! Attempting to reconnect...");

        // Notify the player via Frame (or console fallback)
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                    null,
                    "Connection to the server lost. Attempting to reconnect...",
                    "Connection Lost",
                    JOptionPane.WARNING_MESSAGE
            );
        });

        // Close the old socket connection
        try {
            if (oldSocketChannel != null && oldSocketChannel.isOpen()) {
                System.out.println("Closing old socket connection...");
                oldSocketChannel.close();
            }
        } catch (IOException e) {
            System.err.println("Failed to close old connection: " + e.getMessage());
        }

        // Reconnection loop in a new thread
        new Thread(() -> {
            boolean reconnected = false;
            SocketChannel socketChannel = null;

            while (!reconnected) {
                try {
                    System.out.println("Attempting to reconnect to server...");
                    // Establish a new SocketChannel
                    socketChannel = SocketChannel.open();
                    socketChannel.configureBlocking(true);
                    socketChannel.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT));
                    System.out.println("Reconnection successful!");

                    // Re-initialize the I/O streams
                    DataInputStream in = new DataInputStream(socketChannel.socket().getInputStream());
                    DataOutputStream out = new DataOutputStream(socketChannel.socket().getOutputStream());

                    // Notify the server of reconnection (send the hero's name)
                    String heroName = mapGenerator.getHero().getName();
                    out.writeUTF("Reconnect:" + heroName);
                    out.flush();

                    // Process the server's acknowledgment
                    String response = in.readUTF();
                    System.out.println("Server response after reconnection: " + response);

                    if (response.contains("WELCOME_BACK")) {
                        System.out.println("Reconnection acknowledged by server.");

                        // Resume updates and interactions
                        listenForServerUpdates(socketChannel, mapGenerator);

                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(
                                    null,
                                    "Successfully reconnected to the server!",
                                    "Reconnection Successful",
                                    JOptionPane.INFORMATION_MESSAGE
                            );
                        });
                        reconnected = true; // Exit the loop
                    } else {
                        throw new IOException("Unexpected server response: " + response);
                    }
                    // After successful reconnection:
                    // Request current hero position
                    out.writeUTF("GET_HERO_POSITION");
                    out.flush();

                    // Server responds with "HERO_POSITION:x,y"
                    String positionResponse = in.readUTF();
                    if (positionResponse.startsWith("HERO_POSITION:")) {
                        String[] xy = positionResponse.substring("HERO_POSITION:".length()).split(",");
                        int x = Integer.parseInt(xy[0].trim());
                        int y = Integer.parseInt(xy[1].trim());

                        mapGenerator.getHero().setPosition(new Position(x, y));
                        mapGenerator.repaint(); // Render updated position
                    }
                    // After reconnecting and syncing hero position
                    out.writeUTF("GET_ENEMY_POSITIONS");
                    out.flush();

// Process the response from the server
                    String enemyPositions = in.readUTF();
                    if (enemyPositions.startsWith("ENEMY_UPDATE:")) {
                        String enemyData = enemyPositions.substring("ENEMY_UPDATE:".length());
                        updateEnemyPositionsOnMap(enemyData, mapGenerator);
                        System.out.println("Enemy positions synchronized after reconnection.");
                    }

                } catch (IOException e) {
                    System.err.println("Reconnection failed: " + e.getMessage());
                    System.out.println("Retrying in 3 seconds...");
                    try {
                        Thread.sleep(3000); // Wait 3 seconds before retrying
                    } catch (InterruptedException ignored) {
                    }
                }
            }
        }).start();
    }
    private void createAndShowGUI(SocketChannel socketChannel, DataInputStream in, DataOutputStream out,
                                  MapGenerator mapGenerator) throws IOException {
        JFrame frame = new JFrame("Dungeons");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        // Hero initialization check
        if (mapGenerator.getHero() == null) {
            throw new IllegalStateException("Hero is missing! Cannot initialize GUI.");
        }

        // Add components
        frame.add(mapGenerator.getScrollableMap(), BorderLayout.CENTER);
        ActionsPanel actionsPanel = new ActionsPanel(
                mapGenerator.getHero(), mapGenerator, socketChannel, ByteBuffer.allocate(BUFFER_SIZE), in, out);
        frame.add(actionsPanel, BorderLayout.SOUTH);

        // Force GUI rendering
        frame.setVisible(true);
        frame.revalidate(); // Ensure layout updates
        frame.repaint();    // Refresh components

        // Start server updates in a separate thread
        new Thread(() -> listenForServerUpdates(socketChannel, mapGenerator)).start();
    }
    private void listenForServerUpdates(SocketChannel socketChannel, MapGenerator mapGenerator) {
        try {
            DataInputStream in = new DataInputStream(socketChannel.socket().getInputStream());
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    if (in.available() > 0) {
                        String serverMessage = in.readUTF();
                        System.out.println("Server update received: " + serverMessage);
                        if (serverMessage.startsWith("HERO_POSITION:")) {
                            String positionData = serverMessage.substring("HERO_POSITION:".length());
                            String[] xy = positionData.split(",");
                            int x = Integer.parseInt(xy[0].trim());
                            int y = Integer.parseInt(xy[1].trim());

                            // Update hero's position
                            SwingUtilities.invokeLater(() -> {
                                mapGenerator.getHero().setPosition(new Position(x, y));
                                mapGenerator.repaint(); // Ensure the new position is rendered
                            });
                            return;
                        }
                        // Process server messages on the EDT
                        SwingUtilities.invokeLater(() -> processMessage(serverMessage, mapGenerator));

                        if (serverMessage.startsWith("ENEMY_UPDATE:")) {
                            String enemyData = serverMessage.substring("ENEMY_UPDATE:".length());
                            updateEnemyPositionsOnMap(enemyData, mapGenerator);
                            return;
                        }
                    }
                } catch (IOException e) { // Handle connection loss during reading
                    System.err.println("Error during server communication: " + e.getMessage());
                    handleConnectionLoss(socketChannel, mapGenerator);
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to listen for server updates: " + e.getMessage());
            handleConnectionLoss(socketChannel, mapGenerator);
        }
    }
    private void processMessage(String serverMessage, MapGenerator mapGenerator) {
        // Normalize and trim the message
        serverMessage = serverMessage.trim();

        // Handle "Hero registered" responses
        if (serverMessage.startsWith("Hero registered:")) {
            // Extract the hero's name from the message
            String heroName = serverMessage.substring("Hero registered:".length()).trim();

            // Log success and exit
            System.out.println("Hero successfully registered: " + heroName);
            return;
        }

        // Optionally handle other known server message types
        if (serverMessage.startsWith("MAP_UPDATE")) {
            // Process map update logic here
            System.out.println("Received map update from server: " + serverMessage);
            return;
        }

        // Unknown or unhandled responses
        System.err.println("Unexpected server response: " + serverMessage);
    }
    private void updateEnemyPositionsOnMap(String enemyData, MapGenerator mapGenerator) {
        String[] positions = enemyData.split(";");
        for (String pos : positions) {
            if (!pos.isEmpty()) {
                String[] coords = pos.split(",");
                int row = Integer.parseInt(coords[0].trim());
                int col = Integer.parseInt(coords[1].trim());

                // Update the map display with the enemy position (e.g., "M" for monster)
                mapGenerator.setContentAtPosition(new Position(row, col), "M");
            }
        }

        mapGenerator.repaint(); // Refresh the map display after all updates
    }
    public static GameClient createGameClient() {
        return new GameClient();
    }

}