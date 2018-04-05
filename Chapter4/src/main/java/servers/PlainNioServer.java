package servers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class PlainNioServer
{
    public static void serve(int port) throws IOException
    {
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        ServerSocket serverSocket = serverChannel.socket();
        InetSocketAddress address = new InetSocketAddress(port);
        
        // Binds the server to the selected port
        serverSocket.bind(address);
        System.out.println("Listenining on " + serverSocket.getInetAddress().toString() + ":" + Integer.toString(serverSocket.getLocalPort()));

        // Opens the Selector for handling clients
        Selector selector = Selector.open();

        // Register the ServerSocket with the Selector to accept connections 
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);


        final ByteBuffer msg = ByteBuffer.wrap("Hi!\r\n".getBytes());
        while (true)
        {
            try
            {
                // Wait for new events to process; blocks until the next incoming event
                selector.select();
            }
            catch (IOException e)
            {
                e.printStackTrace();
                // Handle exception
                break;
            }

            // Obtains all SelectionKey instances that received events
            Set<SelectionKey> readyKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = readyKeys.iterator();
            while (iterator.hasNext())
            {
                SelectionKey key = iterator.next();
                iterator.remove();
                try
                {
                    // Checks if the event is a new connection ready to be accepted
                    if (key.isAcceptable())
                    {
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        SocketChannel client = server.accept();
                        client.configureBlocking(false);
                        client.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ, msg.duplicate());
                        System.out.println("Accepted a connection from " + client);
                    }
                    // Checks if the socket is ready for writing data
                    if (key.isWritable())
                    {
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer buffer = (ByteBuffer) key.attachment();
                        while (buffer.hasRemaining())
                        {
                            if (client.write(buffer) == 0) // Writes data to the connected client
                                break;
                        }
                        client.close(); // Closes the connection
                    }
                    
                }
                catch (IOException e)
                {
                    key.cancel();
                    try
                    {
                        key.channel().close();
                    }
                    catch (IOException ex)
                    {
                        // Ignore on close
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws IOException
    {
        serve(13);
    } 
}