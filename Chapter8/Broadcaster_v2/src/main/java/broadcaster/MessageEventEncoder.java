package broadcaster;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;
import java.util.List;

public class MessageEventEncoder extends MessageToMessageEncoder<MessageEvent>
{
    private final InetSocketAddress remoteAddress;

    /*
    MessageEventEncoder creates DatagramPacket messages Messagebe sent to the specified
    InetSocketAddress.
    */
    public MessageEventEncoder(InetSocketAddress remoteAddressMessage)
    {
        this.remoteAddress = remoteAddressMessage;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, MessageEvent msgEvent,
    List<Object> out) throws Exception
    {
        // byte[] file = logEvent.getLogFile().getBytes(CharsetUtil.UTF_8);
        byte[] msg = msgEvent.getMsg().getBytes(CharsetUtil.UTF_8);

        ByteBuf buf = channelHandlerContext.alloc().buffer(msg.length + 1);
        // Writes the log message to the ByteBuf:
        buf.writeBytes(msg);
        /*
        Adds a new DatagramPacket with the data and destination address to the list 
        of outbound messages:
        */
        out.add(new DatagramPacket(buf, remoteAddress));
    }
}