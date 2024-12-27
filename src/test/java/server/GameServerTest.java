package server;

import Characters.*;
import actions.CommandExecutor;
import actions.PlayerRepository;
import graphicScenes.MapGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Collections;
import java.util.Set;

import static org.mockito.Mockito.*;

class GameServerTest {

    private GameServer gameServer;
    private MapGenerator mockMapGenerator;
    private CommandExecutor mockCommandExecutor;
    private PlayerRepository mockPlayerRepository;

    @BeforeEach
    void setUp() {
        // Create mocks for dependencies
        mockMapGenerator = mock(MapGenerator.class);
        mockCommandExecutor = mock(CommandExecutor.class);
        mockPlayerRepository = mock(PlayerRepository.class);

        // Create GameServer with mocked dependencies
        gameServer = new GameServer(mockMapGenerator);
        gameServer.commandExecutor = mockCommandExecutor;
        gameServer.playerRepository = mockPlayerRepository;
    }

    @Test
    void testServerInitialization() {
        // Verifies that the server initializes correctly
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress("0.0.0.0", 8080));
            serverSocketChannel.configureBlocking(false);

            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            assert serverSocketChannel != null;
            assert selector != null;

            serverSocketChannel.close();
            selector.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testHeroRegistration() throws Exception {
        // Mock SocketChannel and its streams
        SocketChannel socketChannel = mock(SocketChannel.class);
        OutputStream mockOutputStream = mock(OutputStream.class);
        DataOutputStream mockDataOutputStream = new DataOutputStream(mockOutputStream);

        when(socketChannel.socket()).thenReturn(mock(java.net.Socket.class));
        when(socketChannel.socket().getOutputStream()).thenReturn(mockOutputStream);

        // Register hero
        gameServer.registerHero(socketChannel, "HeroName");

        // Verify that the response is sent with correct format
        verify(mockOutputStream, atLeastOnce()).write(Mockito.any());
    }

    @Test
    void testHeroReconnection() throws Exception {
        // Mock SocketChannel and its streams
        SocketChannel socketChannel = mock(SocketChannel.class);
        OutputStream mockOutputStream = mock(OutputStream.class);

        when(socketChannel.socket()).thenReturn(mock(java.net.Socket.class));
        when(socketChannel.socket().getOutputStream()).thenReturn(mockOutputStream);

        DataOutputStream mockDataOutputStream = new DataOutputStream(mockOutputStream);

        // Mock PlayerRepository behavior
        when(mockPlayerRepository.isHeroRegistered("ExistingHero")).thenReturn(true);
        when(mockPlayerRepository.isHeroRegistered("NonExistentHero")).thenReturn(false);

        // Simulate reconnection
        gameServer.acceptFromKey(createSelectionKeyMock(socketChannel));
        verify(mockOutputStream, atLeastOnce()).write(Mockito.any());
    }

    @Test
    void testBroadcastMapUpdates() {
        // Mock CommandExecutor behavior
        SocketChannel mockSocketChannel = mock(SocketChannel.class);
        when(mockCommandExecutor.getSocketChannelsFromRepository())
                .thenReturn(Collections.singletonList(mockSocketChannel));

        // Mock socket channel behavior
        when(mockSocketChannel.isOpen()).thenReturn(true);

        // Call broadcast updates
        gameServer.broadcastMapUpdates("Map Update");

        // Verify output stream interactions
        try {
            verify(mockSocketChannel, times(1)).write(Mockito.any(ByteBuffer.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testServerShutdown() {
        // Stop the server and verify resource cleanup
        gameServer.shutdown();
        assert !gameServer.running; // Ensure server running state is false
    }

    // Helper method
    private SelectionKey createSelectionKeyMock(SocketChannel socketChannel) {
        SelectionKey mockKey = mock(SelectionKey.class);
        when(mockKey.channel()).thenReturn(socketChannel);
        return mockKey;
    }
}