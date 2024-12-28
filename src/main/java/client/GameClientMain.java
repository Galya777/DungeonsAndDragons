package client;

import graphicScenes.RegistrationWindow;

import javax.swing.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The main entry point for the game client application.
 * This class is responsible for initializing the game client and launching
 * the registration window in a Swing-based graphical user interface.
 * It sets up the application in the Event Dispatch Thread (EDT) to ensure
 * proper thread safety when working with Swing components.
 */
public class GameClientMain {
    private static final Logger LOGGER = Logger.getLogger(GameClientMain.class.getName());

    public static void main(String[] args) {
        try {
            GameClient gameClient = GameClient.createGameClient();

            SwingUtilities.invokeLater(() -> {
                try {
                    RegistrationWindow registrationWindow = new RegistrationWindow(gameClient);
                    registrationWindow.setVisible(true);
                    LOGGER.info("Registration window launched successfully.");
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error while creating or showing RegistrationWindow", e);
                    JOptionPane.showMessageDialog(null, "Error launching the game: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error in main method", e);
            JOptionPane.showMessageDialog(null, "Unexpected error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}