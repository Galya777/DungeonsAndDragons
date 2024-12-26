package server;

import Characters.*;
import Characters.Hero;
import actions.CommandExecutor;
import actions.PlayerRepository;
import actions.UserRecipient;
import graphicScenes.MapGenerator;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameServer {
    private static final String SERVER_HOST = System.getenv("SERVER_HOST") != null ? System.getenv("SERVER_HOST") : "0.0.0.0";
    private static final int SERVER_PORT = System.getenv("SERVER_PORT") != null ? Integer.parseInt(System.getenv("SERVER_PORT")) : 8080;
    private static final int BUFFER_SIZE = 1024;

    private static final String PROBLEM_OPENING_RESOURCES_MESSAGE = "Problem with opening resources.";
    private static final String PROBLEM_INTERRUPTED_THREAD_MESSAGE = "Server thread was interrupted.";
    private static final String PROBLEM_ACCEPT_MESSAGE = "Problem occurred while accepting a connection.";
    private static final String NOTHING_TO_READ_CLOSING_CHANNEL_MESSAGE = "Nothing to read, closing channel.";
    private static final String PROBLEM_SELECTING_KEYS_MESSAGE = "Problem occurred while selecting keys.";
    private static final String PROBLEM_READING_FROM_CHANNEL_MESSAGE = "Problem occurred while reading from channel.";
    private static final String PROBLEM_CLOSING_CHANNEL_MESSAGE = "Problem occurred while closing channel.";
    private static final String PROBLEM_WRITING_TO_CHANNEL_MESSAGE = "Problem occurred while writing to channel.";

    private static final Logger LOGGER = Logger.getLogger(GameServer.class.getName());

    private CommandExecutor commandExecutor;
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    private ByteBuffer buffer;
    private ExecutorService executorService;
    private PlayerRepository playerRepository;
    private MapGenerator mapGenerator;

    private GameServer(MapGenerator gameRepository) {
        this.mapGenerator = gameRepository;
        this.commandExecutor = new CommandExecutor(gameRepository);
        this.playerRepository = new PlayerRepository();

        // Open resources for the server
        openResources();

        // Initialize the executor service for multithreading
        executorService = Executors.newCachedThreadPool();

        // Add the shutdown hook after resources are opened
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

            // Shut down the executor service
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
            LOGGER.log(Level.SEVERE, PROBLEM_OPENING_RESOURCES_MESSAGE, e);
            throw new RuntimeException(PROBLEM_OPENING_RESOURCES_MESSAGE, e);
        }
    }
    private static int nextHeroId = 1; // Static counter for unique IDs

    private void registerHero(DataInputStream in, DataOutputStream out) throws IOException {
        try {
            LOGGER.info("Starting hero registration...");

            // Step 1: Get the username from the client
            String username = in.readUTF();
            LOGGER.info("Received username: " + username);

            // Step 2: Assign a random free position
            Position randomPosition = mapGenerator.getRandomFreePosition();
            if (randomPosition == null) {
                randomPosition= new Position(0,0);
                LOGGER.warning("No free positions available on the board. Using default position: " + randomPosition);
            }
            LOGGER.info("Assigned random free position: " + randomPosition);

            // Step 3: Generate unique hero ID and assign default stats
            String uniqueId = "HERO-" + nextHeroId++;
            Hero clientHero = new Hero(username, uniqueId, randomPosition, "images/mainChar2.png");

            // Step 4: Register the hero in the game and map
            mapGenerator.setHeroPosition(randomPosition); // Update the map
            mapGenerator.setHero(clientHero); // Track the hero globally
            LOGGER.info("Registered hero: " + clientHero);

            // Step 5: Notify the client of successful registration
            out.writeUTF("Hero registered successfully at position: " + randomPosition);
            out.flush();
        } catch (IllegalStateException e) {
            LOGGER.log(Level.SEVERE, "Critical error: No free positions available on the board.", e);
            out.writeUTF("Error: No free positions available on the board. Registration failed.");
            out.flush();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error during hero registration.", e);
            out.writeUTF("Error: Failed to register hero. Try again later.");
            out.flush();
        }
    }

    public void startGameServer() {
        boolean running = true;
        LOGGER.info("Game server started...");
        while (running) {
            try {
                selector.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                LOGGER.info("Number of selected keys: " + selectedKeys.size());
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    if (key.isReadable()) {
                        LOGGER.info("Processing readable key...");
                        readFromKey(key);
                    } else if (key.isAcceptable()) {
                        LOGGER.info("Processing acceptable key...");
                        acceptFromKey(key);
                    }
                    keyIterator.remove();
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error in selector loop", e);
                throw new RuntimeException(PROBLEM_SELECTING_KEYS_MESSAGE, e);
            }
        }
        executorService.shutdown();
    }
    public void processClientHero(Hero hero) {
        if (hero == null) {
            LOGGER.warning("Cannot process null Hero.");
            throw new IllegalArgumentException("Received invalid Hero from client.");
        }

        // Add the Hero to the MapGenerator
        mapGenerator.setHero(hero);
        LOGGER.info("Hero successfully registered: " + hero);
    }
    private void acceptFromKey(SelectionKey key) {
        try {
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
            SocketChannel socketChannel = serverSocketChannel.accept();

            if (socketChannel != null) {
                socketChannel.configureBlocking(true); // Set blocking for safe transmission

                try (DataInputStream in = new DataInputStream(socketChannel.socket().getInputStream());
                     DataOutputStream out = new DataOutputStream(socketChannel.socket().getOutputStream())) {

                    LOGGER.info("New client connected: " + socketChannel.getRemoteAddress());
                    registerHero(in, out); // Register hero

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
            int readBytes;
            try {
                // Step 1: Read data from the client
                readBytes = socketChannel.read(buffer);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, PROBLEM_READING_FROM_CHANNEL_MESSAGE, e);
                closeChannel(socketChannel);
                return;
            }

            if (readBytes <= 0) {
                LOGGER.info(NOTHING_TO_READ_CLOSING_CHANNEL_MESSAGE);
                closeChannel(socketChannel);
                return;
            }

            // Step 2: Process the command
            buffer.flip();
            String command = new String(buffer.array(), 0, buffer.limit()).trim();
            LOGGER.info("Received command: " + command + " from SocketChannel: " + socketChannel);

            // Call the "answerForCommand" to process the data
            answerForCommand(command, socketChannel);
        });
    }

    private void answerForCommand(String command, SocketChannel socketChannel) {
        try {
            // Step 1: Check if the user (socketChannel) is already registered
            if (!playerRepository.isUserRegistered(socketChannel)) {
                LOGGER.info("Registering new user with command: " + command);

                // Assuming command is "REGISTER", register the Hero manually
                if (command.equalsIgnoreCase("REGISTER")) {
                    try (DataInputStream in = new DataInputStream(socketChannel.socket().getInputStream());
                         DataOutputStream out = new DataOutputStream(socketChannel.socket().getOutputStream())) {

                        // Use the updated registerHero method
                        registerHero(in, out);
                    }
                }

                // Register the user in the PlayerRepository (assuming command is part of user registration logic)
                String response = playerRepository.registerUser(socketChannel, command, mapGenerator);
                sendMessageToChannel(response, socketChannel); // Notify the client

                return;
            }

            // Step 2: If the user is already registered, execute further commands
            UserRecipient userRecipient = new UserRecipient(null, null);
            String commandResult = commandExecutor.executeCommand(command, socketChannel, userRecipient);

            // Step 3: Send messages to other users, if applicable
            String messageForOtherUser = userRecipient.getMessage();
            if (messageForOtherUser != null) {
                sendMessageToChannel(messageForOtherUser, userRecipient.getSocketChannel());
            }

            // Step 4: Respond to the command sender
            sendMessageToChannel(commandResult, socketChannel);

            // Update the game map for all connected users
            String updatedDungeonMap = commandExecutor.getDungeonMapFromRepository();
            Collection<SocketChannel> socketChannels = commandExecutor.getSocketChannelsFromRepository();
            for (SocketChannel socketChannelRecipient : socketChannels) {
                sendMessageToChannel(updatedDungeonMap, socketChannelRecipient);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during command processing: ", e);
        }
    }

    private void sendMessageToChannel(String message, SocketChannel socketChannel) {
        if (socketChannel == null || !socketChannel.isOpen()) {
            LOGGER.warning("SocketChannel is closed or null! Unable to send message: " + message);
            return;
        }

        buffer.clear();
        buffer.put(message.getBytes());
        buffer.flip();

        try {
            socketChannel.write(buffer);
            LOGGER.info("Message sent to " + socketChannel.getRemoteAddress() + ": " + message);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Problem occurred while writing to channel.", e);
            closeChannel(socketChannel);
        }
    }

    private void closeChannel(SocketChannel socketChannel) {
        try {
            if (socketChannel.isOpen()) {
                socketChannel.close();
                LOGGER.info("Closed channel: " + socketChannel.getRemoteAddress());
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Problem occurred while closing channel.", e);
        }
    }

    public static GameServer createGameServer(MapGenerator gameRepository) {
        return new GameServer(gameRepository);
    }

  
}