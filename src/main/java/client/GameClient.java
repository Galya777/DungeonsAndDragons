// Updated GameClient.java
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
import java.util.logging.Logger;

public class GameClient {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;
    private static final int BUFFER_SIZE = 1024;
    private Character hero; // Store the registered hero instance


    public void startGame(String username) {
        SocketChannel socketChannel = null;
        DataOutputStream out = null;
        DataInputStream in = null;

        try {
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(true);
            socketChannel.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT));

            out = new DataOutputStream(socketChannel.socket().getOutputStream());
            in = new DataInputStream(socketChannel.socket().getInputStream());

            out.writeUTF(username);
            out.flush();
            System.out.println("Sent username to server: " + username);

            String response = in.readUTF();
            System.out.println("Server response: " + response);

            if (response.startsWith("Hero registered successfully at position:")) {
                String[] parts = response.split(":");
                if (parts.length > 1) {
                    String[] position = parts[1].trim().split(",");
                    int x = Integer.parseInt(position[0]);
                    int y = Integer.parseInt(position[1]);

                    hero = new Hero(username, "Client-HERO-1", new Position(x, y), "images/mainChar2.png");
                    System.out.println("Hero initialized: " + hero);

                    if (hero != null) {
                        final SocketChannel finalSocket = socketChannel;
                        final DataInputStream finalIn = in;
                        final DataOutputStream finalOut = out;

                        SwingUtilities.invokeLater(() -> createAndShowGUI(finalSocket, finalIn, finalOut));
                    } else {
                        System.err.println("Failed to initialize hero.");
                    }
                }
            } else {
                System.err.println("Unexpected server response: " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createAndShowGUI(SocketChannel socketChannel, DataInputStream in, DataOutputStream out) {
        JFrame frame = new JFrame();
        frame.setTitle("Dungeons");
        ImageIcon icon = new ImageIcon("logo.jpg");
        frame.setIconImage(icon.getImage());
        frame.getContentPane().setBackground(Color.BLACK);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(2000, 2000);

        // Initialize the map and display it
        MapGenerator mapGenerator = new MapGenerator();

        if (hero != null) {
            mapGenerator.setHero((Hero) hero); // Assign the registered hero to the map
        }
        frame.add(mapGenerator, BorderLayout.CENTER);

        // Add the actions panel for player interactions
        ActionsPanel actionsPanel = new ActionsPanel(null, mapGenerator, socketChannel, ByteBuffer.allocate(BUFFER_SIZE), in, out);
        frame.add(actionsPanel, BorderLayout.SOUTH);

        frame.setVisible(true);

        // Start listening for server updates
        new Thread(() -> listenForServerUpdates(socketChannel, mapGenerator)).start();
    }
    private void listenForServerUpdates(SocketChannel socketChannel, MapGenerator mapGenerator) {
        try (DataInputStream in = new DataInputStream(socketChannel.socket().getInputStream())) {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    // Check if there is data available before reading
                    if (socketChannel.isConnected() && in.available() > 0) {
                        String serverMessage = in.readUTF(); // Read updates from the server
                        System.out.println("Server update: " + serverMessage);

                        // Process specific gameplay updates here
                        SwingUtilities.invokeLater(() -> {
                            if (serverMessage.startsWith("MOVE")) {
                                // Example: Update hero position based on server instructions
                                String[] parts = serverMessage.split(" ");
                                int newRow = Integer.parseInt(parts[1]);
                                int newCol = Integer.parseInt(parts[2]);
                                mapGenerator.updatePlayerPosition(hero.getPosition(), new Position(newRow, newCol), (Hero) hero);
                            }
                            // Additional updates like treasure collection or enemy interactions can go here
                        });
                    }
                } catch (EOFException eofException) {
                    // EOFException signifies the server closed the connection, handle it cleanly
                    System.err.println("Connection closed by server.");
                    break; // Exit the loop if the connection is closed
                }
            }
        } catch (IOException e) {
            // Handle any other IO exceptions
            e.printStackTrace();
        } finally {
            // Close the socket connection properly
            try {
                if (socketChannel != null && socketChannel.isOpen()) {
                    socketChannel.close();
                }
            } catch (IOException closeException) {
                closeException.printStackTrace();
            }
        }
    }
    public static GameClient createGameClient() {
        return new GameClient();
    }
}