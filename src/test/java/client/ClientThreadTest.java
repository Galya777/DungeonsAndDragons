package client;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ClientThreadTest {

    /**
     * Test case: Ensures that the `run` method in `ClientThread`
     * correctly reads data from the SocketChannel and writes it to the OutputStream.
     */
    @Test
    void testRunReadsAndWritesData() throws IOException {
        SocketChannel socketChannel = mock(SocketChannel.class);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        OutputStream out = mock(OutputStream.class);

        when(socketChannel.read(buffer)).thenAnswer(invocation -> {
            byte[] data = "Hello".getBytes();
            buffer.put(data, 0, data.length).flip();
            return data.length;
        }).thenReturn(-1); // Indicate end of stream

        ClientThread clientThread = new ClientThread(socketChannel, buffer, out);
        clientThread.run();

        verify(out, times(1)).write("Hello".getBytes());
        verify(socketChannel, times(1)).close();
    }

    /**
     * Test case: Ensures proper handling of an AsynchronousCloseException.
     */
    @Test
    void testRunHandlesAsynchronousCloseException() throws IOException {
        SocketChannel socketChannel = mock(SocketChannel.class);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        OutputStream out = mock(OutputStream.class);

        when(socketChannel.read(buffer)).thenThrow(AsynchronousCloseException.class);

        ClientThread clientThread = new ClientThread(socketChannel, buffer, out);
        clientThread.run();

        verify(out, never()).write(any());
        verify(socketChannel, times(1)).close();
    }

    /**
     * Test case: Ensures that IOException during read/write is properly handled.
     */
    @Test
    void testRunHandlesIOException() throws IOException {
        SocketChannel socketChannel = mock(SocketChannel.class);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        OutputStream out = mock(OutputStream.class);

        when(socketChannel.read(buffer)).thenThrow(IOException.class);

        ClientThread clientThread = new ClientThread(socketChannel, buffer, out);
        clientThread.run();

        verify(out, never()).write(any());
        verify(socketChannel, times(1)).close();
    }

    /**
     * Test case: Ensures that if socketChannel.read(...) returns -1 immediately, no data is written to OutputStream.
     */
    @Test
    void testRunNoDataRead() throws IOException {
        SocketChannel socketChannel = mock(SocketChannel.class);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        OutputStream out = mock(OutputStream.class);

        when(socketChannel.read(buffer)).thenReturn(-1); // Indicate end of stream

        ClientThread clientThread = new ClientThread(socketChannel, buffer, out);
        clientThread.run();

        verify(out, never()).write(any());
        verify(socketChannel, times(1)).close();
    }

    /**
     * Test case: Ensures `socketChannel.close()` is still called even if OutputStream throws IOException.
     */
    @Test
    void testRunOutputStreamThrowsIOException() throws IOException {
        SocketChannel socketChannel = mock(SocketChannel.class);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        OutputStream out = mock(OutputStream.class);

        when(socketChannel.read(buffer)).thenReturn(5, -1); // Read data once, then end stream
        doThrow(IOException.class).when(out).write(any());

        ClientThread clientThread = new ClientThread(socketChannel, buffer, out);
        clientThread.run();

        verify(out, times(1)).write(any());
        verify(socketChannel, times(1)).close();
    }
}