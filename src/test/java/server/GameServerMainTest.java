package server;

import graphicScenes.MapGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class GameServerMainTest {

    @Test
    void testMainMethod() {
        // Arrange: Mock dependencies
        MapGenerator mockMapGenerator = Mockito.mock(MapGenerator.class);
        GameServer mockGameServer = Mockito.mock(GameServer.class);

        // Act: Initialize and test the main method logic
        try (var mapGeneratorMockedStatic = Mockito.mockStatic(MapGenerator.class);
             var gameServerMockedStatic = Mockito.mockStatic(GameServer.class)) {

            mapGeneratorMockedStatic.when(MapGenerator::new).thenReturn(mockMapGenerator);
            gameServerMockedStatic.when(() -> GameServer.createGameServer(mockMapGenerator))
                    .thenReturn(mockGameServer);

            String[] args = {}; // Empty args for the main method
            GameServerMain.main(args);

            // Assert: Verify correct interactions
            Mockito.verify(mockGameServer).startGameServer();
            mapGeneratorMockedStatic.verify(MapGenerator::new);
            gameServerMockedStatic.verify(() -> GameServer.createGameServer(mockMapGenerator));
        }
    }
}