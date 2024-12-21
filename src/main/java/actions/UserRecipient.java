package actions;

import java.nio.channels.SocketChannel;

public class UserRecipient {
    private SocketChannel socketChannel;
    private String message;

    public UserRecipient(SocketChannel socketChannel, String message) {
        this.socketChannel = socketChannel;
        this.message = message;

    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public String getMessage() {
        return message;
    }

    public void setSocketChannel(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void updateMessage(String update) {
        this.message += update;
    }
}
