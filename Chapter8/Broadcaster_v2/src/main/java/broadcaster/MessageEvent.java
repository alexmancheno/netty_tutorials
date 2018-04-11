package broadcaster;

import java.net.InetSocketAddress;

public class MessageEvent
{
    private final InetSocketAddress source;
    private final String msg;
    private final long received;

    // Constructor for outgoing msg
    public MessageEvent(String msg)
    {
        this(null, -1, msg);
    }

    // Constructor for incoming msg
    public MessageEvent(InetSocketAddress source, long received, String msg)
    {
        this.source = source;
        this.msg = msg;
        this.received = received;
    }

    // Returns the InetSocketAddress of the source that sent the MessageEvent
    public InetSocketAddress getSource()
    {
        return source;
    }

    // Returns the msg conents
    public String getMsg()
    {
        return msg;
    }

    // Returns the time which the MessageEvent was received
    public long getReceivedTimestamp()
    {
        return received;
    }
}