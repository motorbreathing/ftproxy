package four.six.ftproxy.ftp;

public class FTPNoAction implements FTPCommand {
    // The 'default' behavior: pass on a command, unmodified, to the server
    // (eg: "user someuser");
    @Override
    public String execute(String line)
    {
        return line;
    }
}
