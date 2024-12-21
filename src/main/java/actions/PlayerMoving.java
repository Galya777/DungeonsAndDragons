package actions;

import Characters.Hero;
import graphicScenes.MapGenerator;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class PlayerMoving extends KeyAdapter {
    private Hero hero;
    private SocketChannel socketChannel;
    private MapGenerator mapGenerator;
    private ByteBuffer bufferSend;

    public PlayerMoving(Hero hero, SocketChannel socketChannel, MapGenerator mapGenerator, ByteBuffer bufferSend) throws IOException {
        this.hero = hero;
        this.socketChannel = socketChannel;
        this.mapGenerator = mapGenerator;
        this.bufferSend = bufferSend;

        if (!socketChannel.isConnected() || !socketChannel.isOpen()) {
            throw new IOException("Socket is not connected or open");
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        String command = null;

        switch (keyCode) {
            case KeyEvent.VK_UP:
                command = "MOVE UP";
                break;
            case KeyEvent.VK_DOWN:
                command = "MOVE DOWN";
                break;
            case KeyEvent.VK_LEFT:
                command = "MOVE LEFT";
                break;
            case KeyEvent.VK_RIGHT:
                command = "MOVE RIGHT";
                break;
        }

        if (command != null) {
            try {
                if (socketChannel != null && socketChannel.isOpen()) { // Check if channel is open
                    bufferSend.clear();
                    bufferSend.put(command.getBytes());
                    bufferSend.flip();
                    socketChannel.write(bufferSend);
                } else {
                    System.err.println("SocketChannel is closed. Unable to send command: " + command);
                }
            } catch (IOException ex) {
                System.err.println("Error while writing to the socket: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
}