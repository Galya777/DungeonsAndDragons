// Updated GameClient.java
package client;

import graphicScenes.ActionsPanel;
import graphicScenes.MapGenerator;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.net.InetSocketAddress;

public class GameClient {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;
    private static final int BUFFER_SIZE = 1024;

    public void startGame(String username) {
        ByteBuffer bufferSend = ByteBuffer.allocate(BUFFER_SIZE);

        try (SocketChannel socketChannel = SocketChannel.open()) {
            // Set non-blocking mode
            socketChannel.configureBlocking(false);

            // Attempt to connect to the server
            socketChannel.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT));

            // Wait until connection is established
            int retryCount = 50; // Limit retries to avoid infinite loops
            while (!socketChannel.finishConnect() && retryCount > 0) {
                Thread.sleep(100); // Wait for connection to finish
                retryCount--;
            }

            if (retryCount == 0 || !socketChannel.isConnected() || !socketChannel.isOpen()) {
                throw new IOException("Failed to connect to the server at " + SERVER_HOST + ":" + SERVER_PORT);
            }

            // Successfully connected, pass the socket to GUI
            createAndShowGUI(socketChannel, bufferSend);
        } catch (IOException | InterruptedException e) {
            System.err.println("Error during connection setup: " + e.getMessage());
            throw new RuntimeException("Problem with client: Unable to connect to server", e);
        }
    }

    private void createAndShowGUI(SocketChannel socketChannel, ByteBuffer bufferSend) {
        JFrame frame = new JFrame();
        frame.setTitle("Dungeons");
        ImageIcon icon = new ImageIcon("logo.jpg");
        frame.setIconImage(icon.getImage());
        frame.getContentPane().setBackground(Color.BLACK);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(2000, 2000);

        MapGenerator mapGenerator = new MapGenerator();
        frame.add(mapGenerator, BorderLayout.CENTER);

        // Pass valid socketChannel to ActionsPanel
        ActionsPanel actionsPanel = new ActionsPanel(null, mapGenerator, socketChannel, bufferSend);
        frame.add(actionsPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    public static GameClient createGameClient() {
        return new GameClient();
    }
}