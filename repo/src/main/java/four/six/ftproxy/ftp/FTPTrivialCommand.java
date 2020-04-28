package four.six.ftproxy.ftp;

public class FTPTrivialCommand implements FTPCommand {
    protected String command;

    public FTPTrivialCommand(String line)
    {
        command = line;
    }

    // The 'default' behavior: pass on a command, unmodified, to the server
    // (eg: "user someuser");
    @Override
    public String execute()
    {
        return command;
    }
}
