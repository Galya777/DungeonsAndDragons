package actions;

import Characters.Hero;
import graphicScenes.MapGenerator;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.Queue;

public class PlayerMoving extends KeyAdapter {
    private Hero hero;
    private SocketChannel socketChannel;
    private MapGenerator mapGenerator;
    private ByteBuffer bufferSend;
    private final Queue<String> commandQueue = new LinkedList<>();
    private static final String SERVER_IP = "127.0.0.1"; // Replace with actual server IP
    private static final int SERVER_PORT = 12345; // Replace with actual server port

    public PlayerMoving(Hero hero, SocketChannel socketChannel, MapGenerator mapGenerator, ByteBuffer bufferSend) throws IOException {
        this.hero = hero;
        this.socketChannel = socketChannel;
        this.mapGenerator = mapGenerator;
        this.bufferSend = bufferSend;

        // Initial validation of socket state
        if (!socketChannel.isConnected() || !socketChannel.isOpen()) {
            throw new IOException("Socket is not connected or open");
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        String command = null;

        // Map key presses to movement commands
        switch (keyCode) {
            case KeyEvent.VK_UP:
                command = "MOVE UP";
                break;
            case KeyEvent.VK_DOWN:
                command = "MOVE DOWN";
                break;
            case KeyEvent.VK_LEFT:
                command = "MOVE LEFT";
                break;
            case KeyEvent.VK_RIGHT:
                command = "MOVE RIGHT";
                break;
        }

        if (command != null) {
            sendCommand(command);
        }
    }

    /**
     * Sends a command to the server or queues it if the connection is unavailable.
     *
     * @param command The movement command to send.
     */
    private void sendCommand(String command) {
        // Directly move the hero on the map
        if (mapGenerator != null) {
            try {
                mapGenerator.moveHero(command); // Move the hero
            } catch (Exception e) {
                System.err.println("Error during movement: " + e.getMessage());
            }
        }

        // (Optional) If you're keeping the server interaction for multiplayer:
        if (socketChannel != null && socketChannel.isOpen() && socketChannel.isConnected()) {
            try {
                bufferSend.clear();
                bufferSend.put(command.getBytes());
                bufferSend.flip();
                int bytesWritten = socketChannel.write(bufferSend);
                if (bytesWritten > 0) {
                    System.out.println("Command sent: " + command);
                } else {
                    System.err.println("No data was written to the socket.");
                }
            } catch (IOException e) {
                System.err.println("Error sending command. Adding to pending queue: " + command);
                commandQueue.add(command); // Queue the failed command
                tryReconnect();
            }
        } else {
            System.err.println("Socket unavailable, queuing command: " + command);
            commandQueue.add(command); // Queue command for later
            tryReconnect();
        }
    }

    /**
     * Processes pending commands in the queue after reconnection.
     */
    private void processPendingCommands() {
        while (!commandQueue.isEmpty() && socketChannel.isConnected() && socketChannel.isOpen()) {
            sendCommand(commandQueue.poll()); // Attempt to send each queued command
        }
    }

    /**
     * Attempts to safely close the current socket connection.
     */
    private void closeSocketSafely() {
        if (socketChannel != null) {
            try {
                System.err.println("Closing SocketChannel due to an error...");
                socketChannel.close();
                System.err.println("SocketChannel successfully closed.");
            } catch (IOException e) {
                System.err.println("Error while closing SocketChannel: " + e.getMessage());
            }
        }
    }

    /**
     * Attempts to reconnect to the server.
     */
    private void tryReconnect() {
        try {
            if (socketChannel != null && socketChannel.isOpen()) {
                socketChannel.close();
            }
            System.out.println("Attempting to reconnect...");
            socketChannel = SocketChannel.open();
            socketChannel.connect(new InetSocketAddress(SERVER_IP, SERVER_PORT)); // Ensure proper server details

            if (socketChannel.isConnected()) {
                System.out.println("Reconnected successfully.");
                processPendingCommands(); // Process any queued commands
            }
        } catch (IOException e) {
            System.err.println("Reconnection attempt failed: " + e.getMessage());
        }
    }
}