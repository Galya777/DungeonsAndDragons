package server;

import Characters.*;
import Characters.Hero;
import actions.CommandExecutor;
import actions.PlayerRepository;
import actions.UserRecipient;
import graphicScenes.MapGenerator;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameServer {
    private static final String SERVER_HOST = System.getenv("SERVER_HOST") != null ? System.getenv("SERVER_HOST") : "0.0.0.0";
    public static final int SERVER_PORT = System.getenv("SERVER_PORT") != null ? Integer.parseInt(System.getenv("SERVER_PORT")) : 8080;
    private static final int BUFFER_SIZE = 1024;

    private static final Logger LOGGER = Logger.getLogger(GameServer.class.getName());

    private CommandExecutor commandExecutor;
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    private ByteBuffer buffer;
    private ExecutorService executorService = Executors.newFixedThreadPool(10);
    private PlayerRepository playerRepository;
    private MapGenerator mapGenerator;

    private boolean enemyMovementStarted = false; // Track if enemy movement has started

    private static int nextHeroId = 1; // Static counter for unique IDs

    private boolean running = true; // Server running state

    private GameServer(MapGenerator gameRepository) {
        this.executorService = Executors.newFixedThreadPool(10);
        this.mapGenerator = gameRepository;
        this.commandExecutor = new CommandExecutor(gameRepository);
        this.playerRepository = new PlayerRepository();

        // Broadcast updates and open resources
        broadcastMapUpdates(String.valueOf(mapGenerator));
        openResources();

        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if (serverSocketChannel != null && serverSocketChannel.isOpen()) {
                    serverSocketChannel.close();
                }
                if (selector != null && selector.isOpen()) {
                    selector.close();
                }
                System.out.println("Server shut down gracefully.");
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error during shutdown", e);
            }

            // Shutdown executorService
            executorService.shutdown();
        }));
    }

    private void openResources() {
        try {
            // Open and configure the server socket channel
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(SERVER_HOST, SERVER_PORT));
            serverSocketChannel.configureBlocking(false); // Non-blocking mode
            System.out.println("Server is running. Waiting for clients to connect...");

            // Open the selector for handling multiple connections
            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            // Initialize the buffer
            buffer = ByteBuffer.allocate(BUFFER_SIZE);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Problem with opening resources.", e);
            throw new RuntimeException("Problem with opening resources.", e);
        }
    }

    private void registerHero(SocketChannel socketChannel, String heroName) {
        try {
            if (!isConnectionValid(socketChannel)) {
                LOGGER.warning("Cannot register hero. Connection is invalid: " + socketChannel);
                return;
            }

            // Construct the success message
            String responseMessage = "Hero registered: " + heroName;

            // Send the message back to the client
            DataOutputStream out = new DataOutputStream(socketChannel.socket().getOutputStream());
            out.writeUTF(responseMessage);
            out.flush();

            System.out.println("Sent to client: " + responseMessage); // Debug confirmation
        } catch (IOException e) {
            LOGGER.warning("Failed to register hero: " + e.getMessage());
        }
    }

    public void startGameServer() {
        LOGGER.info("Game server started...");
        try {
            while (running) {
                try {
                    if (selector == null || !selector.isOpen()) {
                        LOGGER.warning("Selector is closed. Stopping server loop.");
                        break;
                    }

                    selector.select(); // This blocks until a key is ready

                    Set<SelectionKey> selectedKeys = selector.selectedKeys();
                    Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                    while (keyIterator.hasNext()) {
                        SelectionKey key = keyIterator.next();
                        if (key.isValid() && key.isReadable()) {
                            LOGGER.info("Processing readable key...");
                            readFromKey(key);
                        } else if (key.isValid() && key.isAcceptable()) {
                            LOGGER.info("Processing acceptable key...");
                            acceptFromKey(key);
                        }
                        keyIterator.remove(); // Remove each processed key
                    }
                } catch (ClosedSelectorException e) {
                    LOGGER.warning("Selector was closed. Exiting the server loop.");
                    break; // Exit the loop if the selector is closed
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Error in selector loop", e);
                }
            }
        } finally {
            shutdown(); // Ensure proper shutdown
        }
    }
    private boolean isConnectionValid(SocketChannel channel) {
        return channel != null && channel.isOpen() && channel.isConnected();
    }
    private void acceptFromKey(SelectionKey key) {
        try {
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
            SocketChannel socketChannel = serverSocketChannel.accept();

            if (socketChannel != null) {
                socketChannel.configureBlocking(true);

                try (DataInputStream in = new DataInputStream(socketChannel.socket().getInputStream());
                     DataOutputStream out = new DataOutputStream(socketChannel.socket().getOutputStream())) {

                    LOGGER.info("New client connected: " + socketChannel.getRemoteAddress());
                    String heroName = in.readUTF(); // Assume hero name is sent first by the client
                    registerHero(socketChannel, heroName);

                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Error during Hero registration via acceptFromKey", e);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Problem accepting connection", e);
        }
    }

    private void readFromKey(SelectionKey key) {
        executorService.submit(() -> {
            SocketChannel socketChannel = (SocketChannel) key.channel();
            buffer.clear();
            try {
                if (socketChannel.read(buffer) <= 0) {
                    LOGGER.warning("No data to read. Closing channel...");
                    closeChannel(socketChannel);
                    return;
                }
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Client disconnected or error reading data. Closing channel.", e);
                closeChannel(socketChannel);
                return;
            }

            buffer.flip();
            String command = new String(buffer.array(), 0, buffer.limit()).trim();
            LOGGER.info("Received command: " + command);
        });
    }
    private void closeChannel(SocketChannel socketChannel) {
        try {
            if (socketChannel.isOpen()) {
                socketChannel.close();
            }
            LOGGER.info("Closed connection for channel.");
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error closing client channel.", e);
        }
    }

    private void broadcastMapUpdates(String updateMessage) {
        for (SocketChannel socketChannel : commandExecutor.getSocketChannelsFromRepository()) {
            try {
                // Skip clients with invalid connections
                if (!isConnectionValid(socketChannel)) {
                    LOGGER.warning("Skipping closed or invalid socket: " + socketChannel);
                    continue;
                }

                // Send the update to valid clients
                DataOutputStream out = new DataOutputStream(socketChannel.socket().getOutputStream());
                out.writeUTF(updateMessage);
                out.flush();
            } catch (IOException e) {
                LOGGER.warning("Failed to send update to client: " + e.getMessage());
            }
        }
    }

    private void sendMessageToChannel(String message, SocketChannel socketChannel) throws IOException {
        if (socketChannel != null && socketChannel.isOpen()) {
            try {
                buffer.clear();
                buffer.put((message + "\n").getBytes());
                buffer.flip();
                socketChannel.write(buffer);
            } catch (ClosedChannelException e) {
                LOGGER.warning("Attempted to write to a closed SocketChannel: " + socketChannel);
            }
        } else {
            LOGGER.warning("SocketChannel is null or closed. Cannot send message: " + message);
        }
    }

    private void shutdown() {
        running = false;
        try {
            if (selector != null && selector.isOpen()) {
                selector.close();
            }
            if (serverSocketChannel != null && serverSocketChannel.isOpen()) {
                serverSocketChannel.close();
            }
            executorService.shutdown();
            LOGGER.info("Server shut down gracefully.");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error during server shutdown", e);
        }
    }

    public static GameServer createGameServer(MapGenerator gameRepository) {
        return new GameServer(gameRepository);
    }
}