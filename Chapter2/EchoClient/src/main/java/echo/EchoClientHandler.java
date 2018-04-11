package echo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

@Sharable
public class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf>
{
    // Called after the connection to the server is established
    @Override
    public void channelActive(ChannelHandlerContext context)
    {
        context.writeAndFlush(Unpooled.copiedBuffer("Netty rocks!", CharsetUtil.UTF_8));
    }

    // Called when message is received from the server
    @Override
    public void channelRead0(ChannelHandlerContext context, ByteBuf in)
    {
        System.out.println("Client received: " + in.toString(CharsetUtil.UTF_8));
    }

    // Called if an exception is raised during processing
    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause)
    {
        cause.printStackTrace();
        context.close();
    }
}