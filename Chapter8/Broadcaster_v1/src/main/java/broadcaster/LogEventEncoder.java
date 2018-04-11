package broadcaster;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;
import java.util.List;

public class LogEventEncoder extends MessageToMessageEncoder<LogEvent>
{
    private final InetSocketAddress remoteAddress;

    /*
    LogEventEncoder creates DatagramPacket messages to be sent to the specified
    InetSocketAddress.
    */
    public LogEventEncoder(InetSocketAddress remoteAddress)
    {
        this.remoteAddress = remoteAddress;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, LogEvent logEvent,
    List<Object> out) throws Exception
    {
        byte[] file = logEvent.getLogFile().getBytes(CharsetUtil.UTF_8);
        byte[] msg = logEvent.getMsg().getBytes(CharsetUtil.UTF_8);
        // Writes the filename to the ByteBuf:
        ByteBuf buf = channelHandlerContext.alloc().buffer(file.length + msg.length + 1);
        buf.writeBytes(file);
        // Adds a SEPARATOR:
        buf.writeByte(LogEvent.SEPARATOR);
        // Writes the log message to the ByteBuf:
        buf.writeBytes(msg);
        /*
        Adds a new DatagramPacket with the data and destination address to the list 
        of outbound messages:
        */
        out.add(new DatagramPacket(buf, remoteAddress));
    }
}