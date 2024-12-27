package actions;

import Characters.Hero;
import Characters.Position;
import graphicScenes.MapGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import static org.mockito.Mockito.*;

class PlayerMovingTest {

    /**
     * Tests for the PlayerMoving class, specifically for the keyPressed method.
     * The PlayerMoving class handles Hero movement and interactions in a game environment.
     * The keyPressed method processes key events to move the Hero or execute actions
     * such as attacking targets or sending commands to a server.
     */

    @Test
    void testKeyPressed_MoveUp() throws IOException {
        Hero hero = mock(Hero.class);
        MapGenerator mapGenerator = mock(MapGenerator.class);
        SocketChannel socketChannel = mock(SocketChannel.class);
        when(socketChannel.isConnected()).thenReturn(true);
        when(socketChannel.isOpen()).thenReturn(true);
        ByteBuffer bufferSend = ByteBuffer.allocate(256);

        PlayerMoving playerMoving = new PlayerMoving(hero, socketChannel, mapGenerator, bufferSend);

        KeyEvent keyEvent = new KeyEvent(mock(java.awt.Component.class), KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_UP, ' ');

        playerMoving.keyPressed(keyEvent);

        verify(mapGenerator, times(1)).moveHero("MOVE UP");
        assert bufferSend.position() > 0;
    }

    @Test
    void testKeyPressed_MoveDown() throws IOException {
        Hero hero = mock(Hero.class);
        MapGenerator mapGenerator = mock(MapGenerator.class);
        SocketChannel socketChannel = mock(SocketChannel.class);
        when(socketChannel.isConnected()).thenReturn(true);
        when(socketChannel.isOpen()).thenReturn(true);
        ByteBuffer bufferSend = ByteBuffer.allocate(256);

        PlayerMoving playerMoving = new PlayerMoving(hero, socketChannel, mapGenerator, bufferSend);

        KeyEvent keyEvent = new KeyEvent(mock(java.awt.Component.class), KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_DOWN, ' ');

        playerMoving.keyPressed(keyEvent);

        verify(mapGenerator, times(1)).moveHero("MOVE DOWN");
        assert bufferSend.position() > 0;
    }

    @Test
    void testKeyPressed_MoveLeft() throws IOException {
        Hero hero = mock(Hero.class);
        MapGenerator mapGenerator = mock(MapGenerator.class);
        SocketChannel socketChannel = mock(SocketChannel.class);
        when(socketChannel.isConnected()).thenReturn(true);
        when(socketChannel.isOpen()).thenReturn(true);
        ByteBuffer bufferSend = ByteBuffer.allocate(256);

        PlayerMoving playerMoving = new PlayerMoving(hero, socketChannel, mapGenerator, bufferSend);

        KeyEvent keyEvent = new KeyEvent(mock(java.awt.Component.class), KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_LEFT, ' ');

        playerMoving.keyPressed(keyEvent);

        verify(mapGenerator, times(1)).moveHero("MOVE LEFT");
        assert bufferSend.position() > 0;
    }

    @Test
    void testKeyPressed_MoveRight() throws IOException {
        Hero hero = mock(Hero.class);
        MapGenerator mapGenerator = mock(MapGenerator.class);
        SocketChannel socketChannel = mock(SocketChannel.class);
        when(socketChannel.isConnected()).thenReturn(true);
        when(socketChannel.isOpen()).thenReturn(true);
        ByteBuffer bufferSend = ByteBuffer.allocate(256);

        PlayerMoving playerMoving = new PlayerMoving(hero, socketChannel, mapGenerator, bufferSend);

        KeyEvent keyEvent = new KeyEvent(mock(java.awt.Component.class), KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_RIGHT, ' ');

        playerMoving.keyPressed(keyEvent);

        verify(mapGenerator, times(1)).moveHero("MOVE RIGHT");
        assert bufferSend.position() > 0;
    }

    @Test
    void testKeyPressed_Attack() throws IOException {
        Hero hero = mock(Hero.class);
        MapGenerator mapGenerator = mock(MapGenerator.class);
        SocketChannel socketChannel = mock(SocketChannel.class);
        when(socketChannel.isConnected()).thenReturn(true);
        when(socketChannel.isOpen()).thenReturn(true);
        Position targetPosition = new Position(5, 10);
        //when(mapGenerator.getSelectedTargetPosition()).thenReturn(targetPosition);
        ByteBuffer bufferSend = ByteBuffer.allocate(256);

        PlayerMoving playerMoving = new PlayerMoving(hero, socketChannel, mapGenerator, bufferSend);

        KeyEvent keyEvent = new KeyEvent(mock(java.awt.Component.class), KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_SPACE, ' ');

        playerMoving.keyPressed(keyEvent);

        String expectedCommand = "ATTACK:5,10";
        verify(mapGenerator, never()).moveHero(anyString());
        assert bufferSend.position() > 0;

        bufferSend.flip();
        byte[] sentBytes = new byte[bufferSend.limit()];
        bufferSend.get(sentBytes);
        assert new String(sentBytes).equals(expectedCommand);
    }

    @Test
    void testKeyPressed_NoTarget() throws IOException {
        Hero hero = mock(Hero.class);
        MapGenerator mapGenerator = mock(MapGenerator.class);
        SocketChannel socketChannel = mock(SocketChannel.class);
        when(socketChannel.isConnected()).thenReturn(true);
        when(socketChannel.isOpen()).thenReturn(true);
        //when(mapGenerator.getSelectedTargetPosition()).thenReturn(null);
        ByteBuffer bufferSend = ByteBuffer.allocate(256);

        PlayerMoving playerMoving = new PlayerMoving(hero, socketChannel, mapGenerator, bufferSend);

        KeyEvent keyEvent = new KeyEvent(mock(java.awt.Component.class), KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_SPACE, ' ');

        playerMoving.keyPressed(keyEvent);

        verify(mapGenerator, never()).moveHero(anyString());
        assert bufferSend.position() == 0;
    }
}