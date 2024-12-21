package graphicScenes;

import client.GameClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegistrationWindow extends JFrame {
    private JTextField usernameField;
    private JButton playButton;
    private GameClient gameClient;

    public RegistrationWindow(GameClient gameClient) {
        this.gameClient = gameClient;

        setTitle("Game Registration");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create components
        usernameField = new JTextField(15);
        playButton = new JButton("Play");

        // Set layout and add components
        setLayout(new BorderLayout());
        JPanel panel = new JPanel();
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        add(panel, BorderLayout.CENTER);
        add(playButton, BorderLayout.SOUTH);

        // Add action listener to the play button
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText().trim();
                if (!username.isEmpty()) {
                    gameClient.startGame(username);
                    dispose(); // Close the registration window
                } else {
                    JOptionPane.showMessageDialog(RegistrationWindow.this, "Please enter a username.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}