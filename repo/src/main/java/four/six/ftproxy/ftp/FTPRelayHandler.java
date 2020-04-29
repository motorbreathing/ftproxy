package four.six.ftproxy.ftp;

import io.netty.channel.ChannelHandler;

import four.six.ftproxy.netty.TextRelayHandler;

public class FTPRelayHandler extends TextRelayHandler
{
    @Override
    public ChannelHandler getChannelHandler()
    {
        return this;
    }

    @Override
    public String processCommand(String line)
    {
        line = line.trim();
        if (line.length() > 0)
            return FTPCommandFactory.getCommand(line, this).execute();

        return null;
    }

    @Override
    public String processResponse(String line)
    {
        line = line.trim();
        if (line.length() > 0)
            return FTPResponseFactory.getResponse(line, this).process();

        return null;
    }
}
