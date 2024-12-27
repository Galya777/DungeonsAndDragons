package client;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import javax.swing.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GameClientTest {

    @Test
    public void testStartGame_SuccessfulConnectionAndHeroRegistration() throws Exception {
        GameClient gameClient = new GameClient();

        try (MockedStatic<SocketChannel> socketChannelMockedStatic = mockStatic(SocketChannel.class);
             MockedStatic<SwingUtilities> swingUtilitiesMockedStatic = mockStatic(SwingUtilities.class)) {

            SocketChannel mockSocketChannel = mock(SocketChannel.class);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            ByteArrayInputStream input = new ByteArrayInputStream("REGISTERED:HERO_AT(5,10)\n".getBytes());

            socketChannelMockedStatic.when(SocketChannel::open).thenReturn(mockSocketChannel);
            when(mockSocketChannel.socket().getOutputStream()).thenReturn(output);
            when(mockSocketChannel.socket().getInputStream()).thenReturn(input);
            when(mockSocketChannel.isConnected()).thenReturn(true);

            swingUtilitiesMockedStatic.when(() -> SwingUtilities.invokeLater(any(Runnable.class)))
                    .thenAnswer(invocation -> {
                        Runnable runnable = invocation.getArgument(0);
                        runnable.run();
                        return null;
                    });

            gameClient.startGame("testUser");

            String sentData = new String(output.toByteArray());
            assertTrue(sentData.contains("testUser"));
        }
    }

    @Test
    public void testStartGame_UnexpectedServerResponse() throws Exception {
        GameClient gameClient = new GameClient();

        try (MockedStatic<SocketChannel> socketChannelMockedStatic = mockStatic(SocketChannel.class)) {

            SocketChannel mockSocketChannel = mock(SocketChannel.class);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            ByteArrayInputStream input = new ByteArrayInputStream("UNKNOWN:UnexpectedResponse".getBytes());

            socketChannelMockedStatic.when(SocketChannel::open).thenReturn(mockSocketChannel);
            when(mockSocketChannel.socket().getOutputStream()).thenReturn(output);
            when(mockSocketChannel.socket().getInputStream()).thenReturn(input);
            when(mockSocketChannel.isConnected()).thenReturn(true);

            gameClient.startGame("testUser");

            String sentData = new String(output.toByteArray());
            assertTrue(sentData.contains("testUser"));
        }
    }

    @Test
    public void testStartGame_ConnectionFailure() {
        GameClient gameClient = new GameClient();

        try (MockedStatic<SocketChannel> socketChannelMockedStatic = mockStatic(SocketChannel.class)) {

            socketChannelMockedStatic.when(SocketChannel::open).thenThrow(new IOException("Connection failed"));

            assertDoesNotThrow(() -> gameClient.startGame("testUser"));
        }
    }
}