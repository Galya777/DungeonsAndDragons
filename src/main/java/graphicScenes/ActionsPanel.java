package graphicScenes;

import Characters.Hero;
import actions.CommandExecutor;
import actions.PlayerMoving;
import actions.UserRecipient;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ActionsPanel extends JPanel {
    private UserRecipient userRecipient;
    private JTextArea outputArea;
    private CommandExecutor commandExecutor;
    private PlayerMoving playerMoving;
    private DataInputStream in;
    private DataOutputStream out;
    private final SocketChannel socketChannel; // Added as a field for validation.

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
        this.userRecipient = new UserRecipient(null, null);
        this.commandExecutor = new CommandExecutor(mapGenerator);
        this.setLayout(new BorderLayout());

        // Initialize components
        outputArea = new JTextArea(10, 30);
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        // Create buttons for commands
        JButton backpackButton = new JButton("Backpack");
        JButton collectButton = new JButton("Collect");
        JButton giveButton = new JButton("Give");
        JButton battleButton = new JButton("Battle");

        // Add action listeners to buttons
        backpackButton.addActionListener(e -> SwingUtilities.invokeLater(this::openBackpackWindow));
        collectButton.addActionListener(e -> SwingUtilities.invokeLater(() -> executeCommand("COLLECT")));
        giveButton.addActionListener(e -> SwingUtilities.invokeLater(() -> executeCommand("GIVE")));
        battleButton.addActionListener(e -> SwingUtilities.invokeLater(() -> executeCommand("BATTLE")));

        // Create panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(backpackButton);
        buttonPanel.add(collectButton);
        buttonPanel.add(giveButton);
        buttonPanel.add(battleButton);

        // Add components to main panel
        this.add(buttonPanel, BorderLayout.NORTH);
        this.add(scrollPane, BorderLayout.CENTER);

        try {
            // Initialize PlayerMoving
            this.playerMoving = new PlayerMoving(hero, socketChannel, mapGenerator, bufferSend);

            // Add key listener for player movement
            this.addKeyListener(playerMoving);
            this.setFocusable(true);  // Ensure the panel can receive key events

            // Set the input/output streams for serialized communication
            this.in = in;
            this.out = out;

        } catch (IOException e) {
            // Gracefully handle the exception and allow the GUI to still load
            outputArea.append("Failed to initialize PlayerMoving: " + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }

    private void openBackpackWindow() {
        // No changes in this method
        JFrame backpackFrame = new JFrame("Backpack Commands");
        backpackFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        backpackFrame.setSize(400, 200);

        JButton checkButton = new JButton("Check");
        JButton useButton = new JButton("Use");
        JButton throwButton = new JButton("Throw");

        JTextField treasureInputField = new JTextField(15);

        checkButton.addActionListener(e -> executeBackpackCommand("CHECK", null));
        useButton.addActionListener(e -> {
            String treasureName = treasureInputField.getText().trim();
            if (!treasureName.isEmpty()) {
                executeBackpackCommand("USE", treasureName);
            } else {
                JOptionPane.showMessageDialog(backpackFrame, "Please enter a treasure name.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        throwButton.addActionListener(e -> {
            String treasureName = treasureInputField.getText().trim();
            if (!treasureName.isEmpty()) {
                executeBackpackCommand("THROW", treasureName);
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
                // Check if socketChannel is still open
                if (socketChannel == null || !socketChannel.isConnected() || !socketChannel.isOpen()) {
                    throw new IOException("Cannot execute command. The connection to the server is closed.");
                }

                // Send the command to the server
                out.writeUTF(action);
                out.flush();

                // Reading server response
                if (in.available() > 0) { // Prevent indefinite blocking if no data is available
                    String response = in.readUTF(); // Read server's response
                    SwingUtilities.invokeLater(() -> {
                        if (response != null) {
                            outputArea.append("Response: " + response + "\n");
                        } else {
                            outputArea.append("No response received from server.\n");
                        }
                    });
                } else {
                    SwingUtilities.invokeLater(() -> outputArea.append("No response received after sending command.\n"));
                }
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> outputArea.append("Error executing command: " + action + ". Error: " + e.getMessage() + "\n"));
                e.printStackTrace();
            }
        }).start();
    }

    private void executeBackpackCommand(String action, String treasureName) {
        new Thread(() -> {
            try {
                // Check if socketChannel is still open
                if (socketChannel == null || !socketChannel.isConnected() || !socketChannel.isOpen()) {
                    throw new IOException("Cannot execute backpack command. The connection to the server is closed.");
                }

                String command = action;
                if (treasureName != null) {
                    command += " " + treasureName;
                }

                out.writeUTF(command); // Send command to server
                out.flush();

                if (in.available() > 0) { // Prevent blocking indefinitely on read
                    String response = in.readUTF(); // Read server response
                    SwingUtilities.invokeLater(() -> outputArea.append("Backpack Response: " + response + "\n"));
                }
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> outputArea.append("Failed to execute backpack command: " + e.getMessage() + "\n"));
                e.printStackTrace();
            }
        }).start();
    }
}