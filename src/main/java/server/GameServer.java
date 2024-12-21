package server;

import actions.CommandExecutor;
import actions.PlayerRepository;
import actions.UserRecipient;
import graphicScenes.MapGenerator;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameServer {
    private static final String SERVER_HOST = "localhost";
    public static final int SERVER_PORT = 8080;
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
        openResources();
        executorService = Executors.newCachedThreadPool();
    }

    private void openResources() {
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(SERVER_HOST, SERVER_PORT));
            serverSocketChannel.configureBlocking(false);

            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            buffer = ByteBuffer.allocate(BUFFER_SIZE);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, PROBLEM_OPENING_RESOURCES_MESSAGE, e);
            throw new RuntimeException(PROBLEM_OPENING_RESOURCES_MESSAGE, e);
        }
    }

    public void startGameServer() {
        boolean running = true;
        while (running) {
            try {
                selector.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    if (key.isReadable()) {
                        readFromKey(key);
                    } else if (key.isAcceptable()) {
                        acceptFromKey(key);
                    }
                    keyIterator.remove();
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, PROBLEM_SELECTING_KEYS_MESSAGE, e);
                throw new RuntimeException(PROBLEM_SELECTING_KEYS_MESSAGE, e);
            }
        }

        executorService.shutdown();
    }

    private void acceptFromKey(SelectionKey key) {
        try {
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
            SocketChannel socketChannel = serverSocketChannel.accept();
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, PROBLEM_ACCEPT_MESSAGE, e);
            throw new RuntimeException(PROBLEM_ACCEPT_MESSAGE, e);
        }
    }

    private void readFromKey(SelectionKey key) {
        executorService.submit(() -> {
            SocketChannel socketChannel = (SocketChannel) key.channel();
            buffer.clear();
            int readBytes = 0;
            try {
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

            buffer.flip();
            String command = new String(buffer.array(), 0, buffer.limit()).trim();
            LOGGER.info("Received command: " + command + " from SocketChannel: " + socketChannel);
            answerForCommand(command, socketChannel);
        });
    }

    private void answerForCommand(String command, SocketChannel socketChannel) {
        // Check if the user is already registered
        if (!playerRepository.isUserRegistered(socketChannel)) {
            String response = playerRepository.registerUser(socketChannel, command, mapGenerator);
            LOGGER.info("User registered: " + command); // Debugging line
            sendMessageToChannel(response, socketChannel);
            return;
        }

        UserRecipient userRecipient = new UserRecipient(null, null);
        String commandResult = commandExecutor.executeCommand(command, socketChannel, userRecipient);
        String messageForOtherUser = userRecipient.getMessage();
        if (messageForOtherUser != null) {
            sendMessageToChannel(messageForOtherUser, userRecipient.getSocketChannel());
        }

        sendMessageToChannel(commandResult, socketChannel);
        String updatedDungeonMap = commandExecutor.getDungeonMapFromRepository();
        Collection<SocketChannel> socketChannels = commandExecutor.getSocketChannelsFromRepository();
        for (SocketChannel socketChannelRecipient : socketChannels) {
            sendMessageToChannel(updatedDungeonMap, socketChannelRecipient);
        }
    }

    private void sendMessageToChannel(String message, SocketChannel socketChannel) {
        if (!socketChannel.isOpen()) {
            LOGGER.warning("Attempted to write to a closed channel. Message: " + message);
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