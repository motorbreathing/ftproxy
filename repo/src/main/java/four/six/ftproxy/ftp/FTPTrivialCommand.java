package four.six.ftproxy.ftp;

public class FTPTrivialCommand implements FTPCommand {
    protected String command;

    public FTPTrivialCommand(String line)
    {
        command = line.trim();
    }

    // The 'default' behavior: pass on a command, unmodified, to the server
    // (eg: "user someuser");
    @Override
    public String execute()
    {
        return command;
    }

    @Override
    public String getName()
    {
        return getClass().getName();
    }
}
