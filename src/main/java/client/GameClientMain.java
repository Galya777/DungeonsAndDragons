package client;

import graphicScenes.RegistrationWindow;

import javax.swing.*;

public class GameClientMain {

    public static void main(String[] args) {
        GameClient gameClient = GameClient.createGameClient();
        SwingUtilities.invokeLater(() -> {
            RegistrationWindow registrationWindow = new RegistrationWindow(gameClient);
            registrationWindow.setVisible(true);
        });
    }
}
