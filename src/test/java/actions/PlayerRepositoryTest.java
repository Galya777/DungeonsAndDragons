package actions;

import Characters.CharType;
import Characters.Factory;
import Characters.Hero;
import Characters.Position;
import actions.PlayerRepository;
import graphicScenes.MapGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mockStatic;

class PlayerRepositoryTest {

    /**
     * Test class for PlayerRepository, specifically focusing on the registerUser method.
     * The registerUser method is responsible for registering users into the player repository,
     * and it validates conditions such as repository capacity, username uniqueness, and
     * socket channel registration status before adding the new user.
     */

    @Test
    void testRegisterUser_SuccessfulRegistration() {
        // Arrange
        PlayerRepository playerRepository = new PlayerRepository();
        SocketChannel socketChannel = mock(SocketChannel.class);
        MapGenerator mapGenerator = mock(MapGenerator.class);
        String username = "Player1";

        when(mapGenerator.getFreePosition()).thenReturn(new Position(0, 0));
        Factory factoryMock = mock(Factory.class);
        Hero mockHero = mock(Hero.class);
        try (var mockedFactory = mockStatic(Factory.class)) {
            mockedFactory.when(() -> Factory.getActor(CharType.HERO, username, "1", new Position(0, 0)))
                    .thenReturn(mockHero);
        }
        when(mockHero.getFormattedName()).thenReturn(username);

        // Act
        String result = playerRepository.registerUser(socketChannel, username, mapGenerator);

        // Assert
        assertEquals(username + ", welcome to <DUNGEONS ONLINE>! Ready to spill some blood?", result);
        assertEquals(1, playerRepository.getHeroBySocketChannel().size());
        verify(mapGenerator, times(1)).setContentAtPosition(any(Position.class), eq("1"));
    }

    @Test
    void testRegisterUser_FullRepository() {
        // Arrange
        PlayerRepository playerRepository = new PlayerRepository();
        MapGenerator mapGenerator = mock(MapGenerator.class);

        for (int i = 0; i < 9; i++) {
            SocketChannel channel = mock(SocketChannel.class);
            Position fakePosition = new Position(i, i);
            when(mapGenerator.getFreePosition()).thenReturn(fakePosition);
            playerRepository.registerUser(channel, "Player" + i, mapGenerator);
        }

        SocketChannel newSocketChannel = mock(SocketChannel.class);
        String username = "NewPlayer";

        // Act
        String result = playerRepository.registerUser(newSocketChannel, username, mapGenerator);

        // Assert
        assertEquals("Server is full, try again later.", result);
    }

    @Test
    void testRegisterUser_UsernameAlreadyExists() {
        // Arrange
        PlayerRepository playerRepository = new PlayerRepository();
        SocketChannel firstSocketChannel = mock(SocketChannel.class);
        MapGenerator mapGenerator = mock(MapGenerator.class);
        String username = "Player1";

        when(mapGenerator.getFreePosition()).thenReturn(new Position(0, 0));
        playerRepository.registerUser(firstSocketChannel, username, mapGenerator);

        SocketChannel secondSocketChannel = mock(SocketChannel.class);

        // Act
        String result = playerRepository.registerUser(secondSocketChannel, username, mapGenerator);

        // Assert
        assertEquals("Username is taken. Try with another one.", result);
    }

    @Test
    void testRegisterUser_SocketAlreadyRegistered() {
        // Arrange
        PlayerRepository playerRepository = new PlayerRepository();
        SocketChannel socketChannel = mock(SocketChannel.class);
        MapGenerator mapGenerator = mock(MapGenerator.class);
        String username = "Player1";

        when(mapGenerator.getFreePosition()).thenReturn(new Position(0, 0));
        playerRepository.registerUser(socketChannel, username, mapGenerator);

        // Act
        String result = playerRepository.registerUser(socketChannel, "DifferentUsername", mapGenerator);

        // Assert
        assertEquals("You are already registered.", result);
    }
}