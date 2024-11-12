package ui;

//import com.sun.nio.sctp.NotificationHandler;

public class PreloginClient implements Client{
    private String visitorName = null;
    private final String serverUrl;
    private final Repl notificationHandler;
    private State state = State.LOGGED_OUT;
    private final String BLUE = EscapeSequences.SET_TEXT_COLOR_BLUE;
    private final String GREY = EscapeSequences.SET_TEXT_COLOR_DARK_GREY;

    public PreloginClient(String serverUrl, Repl notificationHandler) {
        this.serverUrl = serverUrl;
        this.notificationHandler = notificationHandler;
    }


    @Override
    public String help() {
        //add new lines
        String help = BLUE + "register <USERNAME> <PASSWORD> <EMAIL> ";
        help += GREY + "- creates a new account\n";
        help += BLUE + "login <USERNAME> <PASSWORD> ";
        help += GREY + "- to play chess\n";
        help += BLUE + "quit ";
        help += GREY + "- playing chess\n";
        help += BLUE + "help ";
        help += GREY + "- shows you the possible commands\n";

        return help;
    }

    @Override
    public String eval(String line) {
        return "";
    }
}
