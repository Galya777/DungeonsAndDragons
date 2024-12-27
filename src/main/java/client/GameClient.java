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

                    // Assign the hero
                    Hero hero = new Hero(username, "Client-HERO-1", new Position(x, y), "images/mainChar2.png");
                    System.out.println("Hero initialized: " + hero);

                    MapGenerator mapGenerator = new MapGenerator();
                    mapGenerator.setHero(hero); // Pass Hero to MapGenerator

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
                }
            } else {
                System.err.println("Unexpected server response: " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createAndShowGUI(SocketChannel socketChannel, DataInputStream in, DataOutputStream out, MapGenerator mapGenerator) throws IOException {
        JFrame frame = new JFrame();
        frame.setTitle("Dungeons");
        ImageIcon icon = new ImageIcon("logo.jpg");
        frame.setIconImage(icon.getImage());
        frame.getContentPane().setBackground(Color.BLACK);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(2000, 2000);

        if (mapGenerator.getHero() == null) {
            throw new IllegalStateException("Hero is null during GUI initialization.");
        }

        frame.add(mapGenerator, BorderLayout.CENTER);

        ActionsPanel actionsPanel = new ActionsPanel(    mapGenerator.getHero(),   // Get Hero from MapGenerator
                 mapGenerator, socketChannel, ByteBuffer.allocate(BUFFER_SIZE), in, out);
        frame.add(actionsPanel, BorderLayout.SOUTH);

        frame.setVisible(true);

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
                                mapGenerator.updatePlayerPosition(
                                        mapGenerator.getHero().getPosition(),
                                        new Position(newRow, newCol),
                                        mapGenerator.getHero()
                                );                            }
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