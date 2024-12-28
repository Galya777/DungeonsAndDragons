package graphicScenes;

import Characters.Hero;
import actions.*;
import game.GameState;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * The ActionsPanel class represents a graphical user interface panel that facilitates interaction
 * between the user and the application's functionality, including command execution, server communication,
 * and player movement. It is designed as part of a game application with components for hero actions,
 * communication with a server, and user input handling.
 *
 * Key functionalities include:
 * - Sending user commands (e.g., "COLLECT", "GIVE", "BATTLE") to the server and receiving responses.
 * - Providing a graphical interface for backpack management, such as checking, using, or discarding items.
 * - Player movement handling through keyboard input.
 * - Displaying server responses and messages in a non-editable text area.
 *
 * The panel integrates with other game components, such as the PlayerMoving and CommandExecutor classes,
 * and communicates with a server through socket channels to send and receive data. It uses Swing components
 * for building the user interface.
 *
 * Constructor throws exceptions for invalid hero initialization or if the provided socket channel
 * connection is not open.
 *
 * Usage considerations:
 * - Ensure the provided Hero, MapGenerator, and SocketChannel objects are valid and initialized before
 *   creating an instance of this panel.
 * - All server communication relies on the input and output streams provided during instantiation.
 * - The panel consumes key events for movement, so it should remain in focus during gameplay.
 */
public class ActionsPanel extends JPanel implements AutoCloseable {
    private UserRecipient userRecipient;
    private CommandExecutor commandExecutor;
    private PlayerMoving playerMoving;
    private DataInputStream in;
    private DataOutputStream out;
    private SocketChannel socketChannel; // Added as a field for validation.
    private final GameState gameState;
    private EventLogPanel eventLogPanel; // Added for event logging

    public ActionsPanel(Hero hero, MapGenerator mapGenerator, SocketChannel socketChannel, ByteBuffer bufferSend,
                        DataInputStream in, DataOutputStream out) throws IllegalArgumentException, IOException {
        // Validate Hero object
        if (hero == null) {
            throw new IllegalArgumentException("Hero cannot be null. Please ensure it is properly initialized.");
        }

        // Validate SocketChannel
        if (socketChannel == null || !socketChannel.isConnected() || !socketChannel.isOpen()) {
            throw new IOException("SocketChannel is not connected or open.");
        }

        this.socketChannel = socketChannel;
        this.userRecipient = new UserRecipient(socketChannel, "");
        this.commandExecutor = new CommandExecutor(mapGenerator);
        this.gameState = GameState.getInstance();

        this.setLayout(new BorderLayout());

        // Initialize components

        JScrollPane scrollPane = new JScrollPane();

        // Create buttons for commands
        JButton backpackButton = new JButton("Backpack");
        JButton collectButton = new JButton("Collect");
        JButton giveButton = new JButton("Give");
        JButton battleButton = new JButton("Battle");

        backpackButton.addActionListener(e -> {
          openBackpackWindow(hero, mapGenerator);
            requestFocus();
        });

        // Add action listeners to buttons
        collectButton.addActionListener(e -> {
            CollectCommand collectCommand = new CollectCommand(hero, new String[] {}, mapGenerator);
            String response = collectCommand.execute(userRecipient);
            eventLogPanel.addEvent(response+ "\n"); // Log the event
            requestFocus();
        });


        giveButton.addActionListener(e -> {
            Map<SocketChannel, Hero> newOne = new HashMap<>();
            newOne.put(socketChannel, hero);
            
            // First show dialog for treasure name
            String treasureName = JOptionPane.showInputDialog("Enter the name of the treasure to give:");
            if (treasureName != null && !treasureName.trim().isEmpty()) {
                // Check if treasure exists in backpack
                if (hero.getTreasure(treasureName) != null) {
                    // Now ask for target player
                    String targetPlayer = JOptionPane.showInputDialog("Enter target player ID:");
                    if (targetPlayer != null && !targetPlayer.trim().isEmpty()) {
                        GiveCommand giveCommand = new GiveCommand(hero, new String[] {"GIVE", treasureName, targetPlayer}, newOne);
                        String response = giveCommand.execute(userRecipient);
                        eventLogPanel.addEvent(response+ "\n"); // Log the event
                    }
                } else {
                    eventLogPanel.addEvent("That treasure is not in your backpack."+ "\n"); // Log the event
                }
            }
            requestFocus(); // Maintain focus for keyboard input
        });

        battleButton.addActionListener(e -> {
            BattleCommand battleCommand = new BattleCommand(hero, new String[] {}, mapGenerator);
            String response = battleCommand.execute(userRecipient);
            String  response2 = battleCommand.executeBattleWithMinion();
            eventLogPanel.addEvent(response2 + "\n"); // Log the event
            eventLogPanel.addEvent(response+ "\n"); // Log the event
            requestFocus();
        });
        // Create panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(backpackButton);
        buttonPanel.add(collectButton);
        buttonPanel.add(giveButton);
        buttonPanel.add(battleButton);

        // Add components to main panel
        this.add(buttonPanel, BorderLayout.NORTH);
        this.add(scrollPane, BorderLayout.CENTER);
        eventLogPanel = new EventLogPanel(); // Initialize event log panel
        this.add(eventLogPanel, BorderLayout.SOUTH); // Add event log panel to the layout

        // Initialize PlayerMoving
        this.playerMoving = new PlayerMoving(hero, socketChannel, mapGenerator, bufferSend);

        // Add key listener for player movement
        this.addKeyListener(playerMoving);
        this.setFocusable(true);  // Ensure the panel can receive key events

        // Set the input/output streams for serialized communication
        this.in = in;
        this.out = out;

    }

    private void openBackpackWindow(Hero hero, MapGenerator mapGenerator) {
        // No changes in this method
        JFrame backpackFrame = new JFrame("Backpack Commands");
        backpackFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        backpackFrame.setSize(400, 200);

        JButton checkButton = new JButton("Check");
        JButton useButton = new JButton("Use");
        JButton throwButton = new JButton("Throw");

        JTextField treasureInputField = new JTextField(15);

        checkButton.addActionListener(e -> executeBackpackCommand("CHECK", null, hero, mapGenerator));
        useButton.addActionListener(e -> {
            String treasureName = treasureInputField.getText().trim();
            if (!treasureName.isEmpty()) {
                executeBackpackCommand("USE", treasureName, hero, mapGenerator);
            } else {
                JOptionPane.showMessageDialog(backpackFrame, "Please enter a treasure name.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        throwButton.addActionListener(e -> {
            String treasureName = treasureInputField.getText().trim();
            if (!treasureName.isEmpty()) {
                executeBackpackCommand("THROW", treasureName, hero, mapGenerator);
            } else {
                JOptionPane.showMessageDialog(backpackFrame, "Please enter a treasure name.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel backpackPanel = new JPanel();
        backpackPanel.setLayout(new GridLayout(4, 1, 10, 10));
        backpackPanel.add(checkButton);
        backpackPanel.add(new JLabel("Treasure Name:"));
        backpackPanel.add(treasureInputField);
        backpackPanel.add(useButton);
        backpackPanel.add(throwButton);

        backpackFrame.add(backpackPanel);
        backpackFrame.setVisible(true);
    }


    private void executeCommand(String action) {
        new Thread(() -> {
            try {
                synchronized (out) {
                    // Send the command to the server
                    out.writeUTF(action);
                    out.flush();
                }

                String response;
                synchronized (in) {
                    // Reading server response
                    response = in.readUTF();
                }
                 
                SwingUtilities.invokeLater(() -> {
                    eventLogPanel.addEvent(response+ "\n"); // Log the event
                });
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> {
                    eventLogPanel.addEvent("Error executing command: " + action + ". Error: " + e.getMessage()+ "\n"); // Log the event
                });
                e.printStackTrace();
            }
        }).start();
    }

    private void executeBackpackCommand(String action, String treasureName, Hero hero, MapGenerator mapGenerator) {
        new Thread(() -> {
            // Set the command in userRecipient before executing
            if (treasureName != null) {
                userRecipient.setMessage(action);
                userRecipient.updateMessage(" " + treasureName);
            } else {
                userRecipient.setMessage(action);
            }

            BackpackCommand backpackCommand = new BackpackCommand(hero, treasureName != null ? new String[] {action.toUpperCase(), treasureName} : new String[] {action.toUpperCase()}, mapGenerator);
            String response = backpackCommand.execute(userRecipient);

            // Update UI with response
            SwingUtilities.invokeLater(() -> {
                if (response != null && !response.isEmpty()) {
                    eventLogPanel.addEvent("Backpack Response: " + response+ "\n"); // Log the event
                } else {
                    eventLogPanel.addEvent("No response from backpack command"+ "\n"); // Log the event
                }
            });
        }).start();
    }

    private boolean validateConnection() {
        return socketChannel != null && socketChannel.isConnected() && socketChannel.isOpen();
    }

    private boolean reconnectToServer() {
        try {
            if (socketChannel != null && socketChannel.isOpen()) {
                socketChannel.close();
            }
            socketChannel = SocketChannel.open();
            socketChannel.connect(new InetSocketAddress("localhost", 8080));
            return true;
        } catch (IOException e) {
            System.err.println("Reconnection failed: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void close() throws Exception {
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.flush();
                out.close();
            }
            if (socketChannel != null && socketChannel.isOpen()) {
                socketChannel.close();
            }
        } catch (IOException e) {
            throw new Exception("Failed to close resources: " + e.getMessage());
        }
    }
}
