package ui;

public class PostloginClient implements Client{

    private String visitorName = null;
    private final String serverUrl;
    private final Repl notificationHandler;
    private State state = State.LOGGED_IN;
    private final String GREEN = EscapeSequences.SET_TEXT_COLOR_GREEN;
    private final String GREY = EscapeSequences.SET_TEXT_COLOR_DARK_GREY;


    public PostloginClient(String serverUrl, Repl notificationHandler) {
        this.serverUrl = serverUrl;
        this.notificationHandler = notificationHandler;
    }

    @Override
    public String help() {
        String help = GREEN + "create <NAME> ";
        help += GREY + "- creates a new game with the provided game name\n";
        help += GREEN + "list ";
        help += GREY + "- lists all the games\n";
        help += GREEN + "join <ID> [WHITE|BLACK]";
        help += GREY + "- joins a game as the specified color\n";
        help += GREEN + "observe <ID> ";
        help += GREY + "- joins a game as an observer\n";
        help += GREEN + "logout ";
        help += GREY + "- logs you out of chess\n";
        help += GREEN + "quit ";
        help += GREY + "- playing chess";
        help += GREEN + "help ";
        help += GREY + "- possible commands\n";

        return help;
    }

    @Override
    public String eval(String line) {
        return "";
    }
}
