package actions;

import java.nio.channels.SocketChannel;

/**
 * The UserRecipient class represents a data model for handling a recipient
 * within a user communication context in the application.
 * It encapsulates a recipient's associated SocketChannel and the message
 * intended to be sent or updated, providing necessary getter, setter,
 * and utility methods for modification and access.
 */
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
