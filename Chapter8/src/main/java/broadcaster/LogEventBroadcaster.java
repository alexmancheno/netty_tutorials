package broadcaster;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;

public class LogEventBroadcaster
{
    private final EventLoopGroup group;
    private final Bootstrap bootstrap;
    private final File file;

    public LogEventBroadcaster(InetSocketAddress address, File file)
    {
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap
            // Boostraps the NioDatagramChannel (connectionless):
            .group(group)
            .channel(NioDatagramChannel.class)
            // Sets the SO_BROADCAST socket option:
            .option(ChannelOption.SO_BROADCAST, true)
            .handler(new LogEventEncoder(address));
        this.file = file;
    }

    public void run() throws Exception
    {
        // Binds the channel
        Channel channel = bootstrap.bind(0).sync().channel();
        long pointer = 0;
        while (true) // Starts the main processing loop
        {
            long len = file.length();
            if (len < pointer)
            {
                // File was reset
                pointer = len; // If necessary, sets the file pointer to the last byte of the file
            }
            else if (len > pointer)
            {
                // Content was added
                RandomAccessFile raf = new RandomAccessFile(file, "r");
                // Sets the current file pointer so nothing old is sent:
                raf.seek(pointer);
                String line;
                while((line = raf.readLine()) != null)
                {
                    // For each long entry, writes a LogEvent to the channel
                    channel.writeAndFlush(new LogEvent(null, -1, file.getAbsolutePath(), line));
                }
                // Stores the current position within the file:
                pointer = raf.getFilePointer();
                raf.close();
            }
            try 
            {
                // Sleeps for 1 second. If interrupted, exit the loop. Else, restart it:
                Thread.sleep(1000);
            } catch (InterruptedException e) 
            {
                Thread.interrupted();
                break;
            }
        }
    }

    public void stop()
    {
        group.shutdownGracefully();
    }

    public static void main(String[] args) throws Exception
    {
        if (args.length != 2)
        {
            throw new IllegalArgumentException();
        }

        LogEventBroadcaster broadcaster = new LogEventBroadcaster(
            new InetSocketAddress("255.255.255.255", Integer.parseInt(args[0])),
            new File(args[1])
        );

        try 
        {
             broadcaster.run();
        }
        finally
        {
            broadcaster.stop();
        }
    }
}