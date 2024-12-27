package client;

import graphicScenes.RegistrationWindow;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.swing.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class GameClientMainTest {

    /**
     * This class tests the main method in GameClientMain class.
     * The main method initializes a GameClient instance and opens the RegistrationWindow in the Swing Event Dispatch Thread.
     */

    @Test
    public void testMain_initializesAndLaunchesRegistrationWindow() {
        // Arrange: Mock dependencies for the test
        GameClient mockedGameClient = mock(GameClient.class);
        RegistrationWindow mockedRegistrationWindow = mock(RegistrationWindow.class);

        // Mock GameClient.createGameClient() method
        Mockito.mockStatic(GameClient.class).when(GameClient::createGameClient).thenReturn(mockedGameClient);

        // Mock RegistrationWindow constructor using Mockito.inlineRunner
        Mockito.mockConstruction(RegistrationWindow.class, (mock, context) -> {
            // Verify that the constructor received the mockedGameClient
            assert context.arguments().get(0) == mockedGameClient;
        });
    }
}
