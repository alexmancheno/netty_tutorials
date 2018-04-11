package broadcaster;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.net.InetSocketAddress;

import java.util.Scanner;

public class Broadcaster
{
    private final EventLoopGroup group;
    private final Bootstrap bootstrap;

    public Broadcaster(InetSocketAddress address)
    {
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap
            // Boostraps the NioDatagramChannel (connectionless):
            .group(group)
            .channel(NioDatagramChannel.class)
            // Sets the SO_BROADCAST socket option:
            .option(ChannelOption.SO_BROADCAST, true)
            .handler(new MessageEventEncoder(address));
    }

    public void run() throws Exception
    {
        // Binds the channel
        Channel channel = bootstrap.bind(0).sync().channel();

        Scanner reader = new Scanner(System.in);
        String line;
        while (true)
        {
            line = reader.nextLine();
            if (line.equals("quit")) break;
            channel.writeAndFlush(new MessageEvent(line));
        }
    }

    public void stop()
    {
        group.shutdownGracefully();
    }

    public static void main(String[] args) throws Exception
    {
        if (args.length != 1)
        {
            throw new IllegalArgumentException();
        }

        Broadcaster broadcaster = new Broadcaster(new InetSocketAddress("239.255.0.1", Integer.parseInt(args[0])));

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