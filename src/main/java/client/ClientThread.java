package client;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.SocketChannel;

/**
 * A thread responsible for handling communication with a client
 * using a non-blocking SocketChannel. This thread continuously reads
 * data from the provided SocketChannel, writes it into a ByteBuffer,
 * and then forwards the processed data to an OutputStream.
 *
 * The thread stops execution if the end of the stream is reached, or
 * if an exception occurs during the read or write operations.
 *
 * Proper resource cleanup is performed by closing the socket channel
 * during the termination of the thread, either after completion or
 * in case of an exception.
 */
class ClientThread extends Thread {
    private SocketChannel socketChannel;
    private ByteBuffer buffer;
    private OutputStream out;

    public ClientThread(SocketChannel socketChannel, ByteBuffer buffer, OutputStream out) {
        this.socketChannel = socketChannel;
        this.buffer = buffer;
        this.out = out;
    }

    @Override
    public void run() {
        try {
            while (true) {
                buffer.clear();
                int bytesRead = socketChannel.read(buffer);
                if (bytesRead == -1) {
                    break; // End of stream; socket was closed
                }

                buffer.flip();
                byte[] bytes = new byte[buffer.remaining()];
                buffer.get(bytes);
                out.write(bytes);
            }
        } catch (AsynchronousCloseException e) {
            System.out.println("Socket channel was closed asynchronously: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socketChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}