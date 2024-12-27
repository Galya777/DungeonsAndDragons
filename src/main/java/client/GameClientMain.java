package client;

import graphicScenes.RegistrationWindow;

import javax.swing.*;

/**
 * The main entry point for the game client application.
 * This class is responsible for initializing the game client and launching
 * the registration window in a Swing-based graphical user interface.
 * It sets up the application in the Event Dispatch Thread (EDT) to ensure
 * proper thread safety when working with Swing components.
 */
public class GameClientMain {

    public static void main(String[] args) {
        GameClient gameClient = GameClient.createGameClient();
        SwingUtilities.invokeLater(() -> {
            RegistrationWindow registrationWindow = new RegistrationWindow(gameClient);
            registrationWindow.setVisible(true);
        });
    }
}
