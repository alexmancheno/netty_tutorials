package broadcaster;

import java.net.InetSocketAddress;

public class LogEvent
{
    public static final byte SEPARATOR = (byte) ':';
    private final InetSocketAddress source;
    private final String logfile;
    private final String msg;
    private final long received;

    // Constructor for outgoing msg
    public LogEvent(String logfile, String msg)
    {
        this(null, -1, logfile, msg);
    }

    // Constructor for incoming msg
    public LogEvent(InetSocketAddress source, long received, String logfile, String msg)
    {
        this.source = source;
        this.logfile = logfile;
        this.msg = msg;
        this.received = received;
    }

    // Returns the InetSocketAddress of the source that sent the LogEvent
    public InetSocketAddress getSource()
    {
        return source;
    }

    // Returns the name of the log file for which the LogEvent was sent
    public String getLogFile()
    {
        return logfile;
    }

    // Returns the msg conents
    public String getMsg()
    {
        return msg;
    }

    // Returns the time which the LogEvent was received
    public long getReceivedTimestamp()
    {
        return received;
    }
}