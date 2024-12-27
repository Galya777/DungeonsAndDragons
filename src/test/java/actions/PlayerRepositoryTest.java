package actions;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import Characters.Position;
import actions.PlayerRepository;
import graphicScenes.MapGenerator;
import org.junit.jupiter.api.Test;

import java.nio.channels.SocketChannel;

public class PlayerRepositoryTest {

    @Test
    public void testRegisterUser_SuccessfulRegistration() {
        // Arrange
        PlayerRepository playerRepository = new PlayerRepository();
        SocketChannel mockSocketChannel = mock(SocketChannel.class);
        MapGenerator mockMapGenerator = mock(MapGenerator.class);
        String username = "Player1";

        when(mockMapGenerator.getFreePosition()).thenReturn(new Position(0, 0));

        // Act
        String result = playerRepository.registerUser(mockSocketChannel, username, mockMapGenerator);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains(username));
        verify(mockMapGenerator, times(1)).getFreePosition();
    }

    @Test
    public void testRegisterUser_RepositoryIsFull() {
        // Arrange
        PlayerRepository playerRepository = new PlayerRepository();
        MapGenerator mockMapGenerator = mock(MapGenerator.class);

        for (int i = 0; i < 9; i++) {
            SocketChannel mockSocketChannel = mock(SocketChannel.class);
            when(mockMapGenerator.getFreePosition()).thenReturn(new Position(0, i));
            playerRepository.registerUser(mockSocketChannel, "Player" + i, mockMapGenerator);
        }

        SocketChannel newSocketChannel = mock(SocketChannel.class);

        // Act
        String result = playerRepository.registerUser(newSocketChannel, "NewPlayer", mockMapGenerator);

        // Assert
        assertEquals("Server is full, try again later.", result);
    }

    @Test
    public void testRegisterUser_UsernameAlreadyTaken() {
        // Arrange
        PlayerRepository playerRepository = new PlayerRepository();
        SocketChannel mockSocketChannel1 = mock(SocketChannel.class);
        SocketChannel mockSocketChannel2 = mock(SocketChannel.class);
        MapGenerator mockMapGenerator = mock(MapGenerator.class);
        String username = "Player1";

        when(mockMapGenerator.getFreePosition()).thenReturn(new Position(0, 0));
        playerRepository.registerUser(mockSocketChannel1, username, mockMapGenerator);

        // Act
        String result = playerRepository.registerUser(mockSocketChannel2, username, mockMapGenerator);

        // Assert
        assertEquals("Username is taken. Try with another one.", result);
    }

    @Test
    public void testRegisterUser_AlreadyRegisteredSocketChannel() {
        // Arrange
        PlayerRepository playerRepository = new PlayerRepository();
        SocketChannel mockSocketChannel = mock(SocketChannel.class);
        MapGenerator mockMapGenerator = mock(MapGenerator.class);
        String username1 = "Player1";
        String username2 = "Player2";

        when(mockMapGenerator.getFreePosition()).thenReturn(new Position(0, 0));
        playerRepository.registerUser(mockSocketChannel, username1, mockMapGenerator);

        // Act
        String result = playerRepository.registerUser(mockSocketChannel, username2, mockMapGenerator);

        // Assert
        assertEquals("You are already registered.", result);
    }

    @Test
    public void testRegisterUser_HeroAddedToRepository() {
        // Arrange
        PlayerRepository playerRepository = new PlayerRepository();
        SocketChannel mockSocketChannel = mock(SocketChannel.class);
        MapGenerator mockMapGenerator = mock(MapGenerator.class);
        String username = "Player1";

        when(mockMapGenerator.getFreePosition()).thenReturn(new Position(0, 0));

        // Act
        playerRepository.registerUser(mockSocketChannel, username, mockMapGenerator);
        boolean isRegistered = playerRepository.isUserRegistered(mockSocketChannel);

        // Assert
        assertTrue(isRegistered);
    }
}