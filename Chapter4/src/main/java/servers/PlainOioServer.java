package servers;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

public class PlainOioServer
{
    public static void server(int port) throws IOException
    {
        final ServerSocket socket = new ServerSocket(port);
        System.out.println("Listenining on " + socket.getInetAddress().toString() + ":" + Integer.toString(socket.getLocalPort()));
        try
        {
            while (true)
            {
                final Socket clientSocket = socket.accept();
                System.out.println("Accepted connection from " + clientSocket);
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        OutputStream out;
                        try
                        {
                            out = clientSocket.getOutputStream();
                            out.write("Hi!\r\n".getBytes(Charset.forName("UTF-8")));
                            out.flush();
                            clientSocket.close();
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                        finally
                        {
                            try
                            {
                                clientSocket.close();
                            }
                            catch (IOException e)
                            {
                                // Ignore on close
                            }
                        }
                    }
                }).start();
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException
    {
        server(13);
    }
}