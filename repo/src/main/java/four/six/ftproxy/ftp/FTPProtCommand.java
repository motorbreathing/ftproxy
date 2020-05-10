package four.six.ftproxy.ftp;

public class FTPProtCommand extends FTPTrivialCommand {
    public static final String COMMAND_STR = "PROT";

    public FTPProtCommand(String args[], FTPRelayHandler handler) {
        super(args, handler);
    }

    @Override
    public String execute() {
        if (args[1].equalsIgnoreCase("P")) {
            handler.dataSSLRequested(true);
        } else if (args[1].equalsIgnoreCase("C")) {
            handler.dataSSLDisableRequested(true);
        }

        return super.execute();
    }
}
