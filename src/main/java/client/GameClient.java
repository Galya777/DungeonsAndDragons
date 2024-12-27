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

            if (response.startsWith("REGISTERED:HERO_AT")) {
                try {
                    // Parse the position from the response (e.g., REGISTERED:HERO_AT(x,y))
                    String positionData = response.substring(response.indexOf('(') + 1, response.indexOf(')'));
                    String[] position = positionData.split(",");
                    int x = Integer.parseInt(position[0].trim());
                    int y = Integer.parseInt(position[1].trim());

                    // Create the hero with the given position
                    Hero hero = new Hero(username, "Client-HERO-1", new Position(x, y), "images/mainChar2.png");
                    System.out.println("Hero initialized: " + hero);

                    // Set up the MapGenerator with the hero
                    MapGenerator mapGenerator = new MapGenerator();
                    mapGenerator.setHero(hero);

                    // Pass everything to your GUI
                    final SocketChannel finalSocketChannel = socketChannel;
                    final DataInputStream finalIn = in;
                    final DataOutputStream finalOut = out;
                    SwingUtilities.invokeLater(() -> {
                        try {
                            createAndShowGUI(finalSocketChannel, finalIn, finalOut, mapGenerator);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                } catch (Exception e) {
                    System.err.println("Failed to parse server response: " + response);
                    e.printStackTrace();
                }
            } else {
                System.err.println("Unexpected server response: " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private boolean isConnectionValid(SocketChannel channel) {
        return channel != null && channel.isOpen() && channel.isConnected();
    }
    private void handleConnectionLoss() {
        System.err.println("Connection lost! Restart or check the server.");
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null, "Connection lost! The game will now close.");
            System.exit(1); // Exit if the connection is lost
        });
    }
    private void createAndShowGUI(SocketChannel socketChannel, DataInputStream in, DataOutputStream out, MapGenerator mapGenerator) throws IOException {
        JFrame frame = new JFrame();
        frame.setTitle("Dungeons");
        ImageIcon icon = new ImageIcon("logo.jpg");
        frame.setIconImage(icon.getImage());
        frame.getContentPane().setBackground(Color.BLACK);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(2000, 2000);

        // Ensure the hero is properly initialized before starting the map
        if (mapGenerator.getHero() == null) {
            throw new IllegalStateException("Hero is null during GUI initialization.");
        }

        frame.add(mapGenerator, BorderLayout.CENTER);

        ActionsPanel actionsPanel = new ActionsPanel(
                mapGenerator.getHero(), mapGenerator, socketChannel, ByteBuffer.allocate(BUFFER_SIZE), in, out);
        frame.add(actionsPanel, BorderLayout.SOUTH);

        frame.setVisible(true);

        // Start listening for updates from the server
        new Thread(() -> listenForServerUpdates(socketChannel, mapGenerator)).start();
    }

    private void listenForServerUpdates(SocketChannel socketChannel, MapGenerator mapGenerator) {
        try {
            DataInputStream in = new DataInputStream(socketChannel.socket().getInputStream());
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    // Validate the connection before attempting to read
                    if (!isConnectionValid(socketChannel)) {
                        System.err.println("Connection invalid. Stopping listener...");
                        handleConnectionLoss();
                        return; // Exit the thread
                    }

                    // Proceed with reading if the connection is valid
                    if (in.available() > 0) {
                        String serverMessage = in.readUTF();
                        System.out.println("Server update: " + serverMessage);
                        processMessage(serverMessage, mapGenerator);
                    }
                } catch (EOFException e) {
                    System.err.println("EOF reached. Server likely closed the connection.");
                    handleConnectionLoss();
                    return; // Exit on EOFException
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            handleConnectionLoss();
        }
    }
    private void processMessage(String serverMessage, MapGenerator mapGenerator) {
        // Handle "Hero registered: [HeroName]" response
        if (serverMessage.startsWith("Hero registered:")) {
            // Extract and print the hero’s name
            String heroName = serverMessage.substring("Hero registered:".length()).trim();
            System.out.println("Hero successfully registered: " + heroName);

            // Exit here as we already processed the message
            return;
        }

        // Add handling for other common server messages
        // For example, MAP_UPDATE or other commands can also be parsed here
        if (serverMessage.startsWith("MAP_UPDATE")) {
            // Example placeholder handling for map updates
            System.out.println("Map update received from server: " + serverMessage);
            return;
        }

        // If no known patterns match, log it as an unexpected response
        System.err.println("Unexpected server response: " + serverMessage);
    }
    private void updateEnemyPositionsOnMap(String enemyData, MapGenerator mapGenerator) {
        String[] positions = enemyData.split(";");
        for (String pos : positions) {
            if (!pos.isEmpty()) {
                String[] coords = pos.split(",");
                int row = Integer.parseInt(coords[0]);
                int col = Integer.parseInt(coords[1]);

                // Update the map with the new enemy position
                mapGenerator.setContentAtPosition(new Position(row, col), "M");
            }
        }

        mapGenerator.repaint(); // Refresh the map display
    }
    public static GameClient createGameClient() {
        return new GameClient();
    }
}