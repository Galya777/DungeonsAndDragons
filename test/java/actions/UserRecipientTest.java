package actions;

import org.junit.jupiter.api.Test;

import java.nio.channels.SocketChannel;
import java.nio.channels.spi.AbstractSelectableChannel;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserRecipientTest {

    @Test
    void testUpdateMessageAppendsTextToExistingMessage() {
        SocketChannel mockSocketChannel = mock(SocketChannel.class, withSettings().defaultAnswer(CALLS_REAL_METHODS));
        String initialMessage = "Hello";
        String update = " World!";
        UserRecipient userRecipient = new UserRecipient(mockSocketChannel, initialMessage);

        // Act
        userRecipient.updateMessage(update);

        // Assert
        assertEquals("Hello World!", userRecipient.getMessage());
    }

    @Test
    void testUpdateMessageWithEmptyUpdateKeepsOriginalMessage() {
        // Arrange
        SocketChannel mockSocketChannel = mock(SocketChannel.class, withSettings().defaultAnswer(CALLS_REAL_METHODS));
        String initialMessage = "Message";
        UserRecipient userRecipient = new UserRecipient(mockSocketChannel, initialMessage);

        // Act
        userRecipient.updateMessage("");

        // Assert
        assertEquals("Message", userRecipient.getMessage());
    }

    @Test
    void testUpdateMessageWithNullThrowsException() {
        // Arrange
        SocketChannel mockSocketChannel = mock(SocketChannel.class, withSettings().defaultAnswer(CALLS_REAL_METHODS));
        String initialMessage = "Error";
        UserRecipient userRecipient = new UserRecipient(mockSocketChannel, initialMessage);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> userRecipient.updateMessage(null));
    }

    @Test
    void testUpdateMessageWorksWithEmptyInitialMessage() {
        // Arrange
        SocketChannel mockSocketChannel = mock(SocketChannel.class, withSettings().defaultAnswer(CALLS_REAL_METHODS));
        String initialMessage = "";
        String update = "New Message";
        UserRecipient userRecipient = new UserRecipient(mockSocketChannel, initialMessage);

        // Act
        userRecipient.updateMessage(update);

        // Assert
        assertEquals("New Message", userRecipient.getMessage());
    }

    @Test
    void testUpdateMessageAppendsMultipleUpdatesSequentially() {
        // Arrange
        SocketChannel mockSocketChannel = mock(SocketChannel.class, withSettings().defaultAnswer(CALLS_REAL_METHODS));
        String initialMessage = "Start";
        UserRecipient userRecipient = new UserRecipient(mockSocketChannel, initialMessage);
        String update1 = " Middle";
        String update2 = " End";

        // Act
        userRecipient.updateMessage(update1);
        userRecipient.updateMessage(update2);

        // Assert
        assertEquals("Start Middle End", userRecipient.getMessage());
    }
}