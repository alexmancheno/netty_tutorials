package servers;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.oio.OioServerSocketChannel;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

public class NettyOioServer
{
    public static void server(int port) throws Exception
    {
        final ByteBuf buf = Unpooled.unreleasableBuffer(
            Unpooled.copiedBuffer("Hi, from a NettyNioServer!\r\n", Charset.forName("UTF-8"))
        );
        EventLoopGroup group = new OioEventLoopGroup();
        try
        {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap  
                .group(group)
                .channel(OioServerSocketChannel.class)
                .localAddress(new InetSocketAddress(port))
                .childHandler(new ChannelInitializer<SocketChannel>()
                {
                    @Override
                    public void initChannel(SocketChannel channel) throws Exception
                    {
                        channel.pipeline().addLast(new ChannelInboundHandlerAdapter() 
                        {
                            @Override
                            public void channelActive(ChannelHandlerContext context) throws Exception
                            {
                                context.writeAndFlush(buf.duplicate()).addListener(ChannelFutureListener.CLOSE);
                            }
                        });
                    }
                });
            System.out.println("Listening on " + bootstrap.config().localAddress());
            ChannelFuture future = bootstrap.bind().sync();
            future.channel().closeFuture().sync();
        }
        finally
        {
            group.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws Exception
    {
        server(13);
    }
}