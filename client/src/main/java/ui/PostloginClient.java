package ui;

import chess.ChessGame;
import model.GameData;
import serverFacade.ServerFacade;

import java.util.ArrayList;
import java.util.List;

public class PostloginClient implements Client{

    private String visitorName = null;
    private final String serverUrl;
    private final Repl notificationHandler;
    private ServerFacade serverFacade;
    private State state = State.LOGGED_IN;
    private final String GREEN = EscapeSequences.SET_TEXT_COLOR_GREEN;
    private final String GREY = EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY;
    private final String RED = EscapeSequences.SET_TEXT_COLOR_RED;
    private List<Integer> intList = new ArrayList<>();

    public PostloginClient(String serverUrl, Repl notificationHandler, ServerFacade serverFacade) {
        this.serverUrl = serverUrl;
        this.notificationHandler = notificationHandler;
        this.serverFacade = serverFacade;
    }

    @Override
    public State getState() {
        return state;
    }

    @Override
    public String help() {
        String help = GREEN + "\ncreate <NAME> ";
        help += GREY + "- creates a new game with the provided game name\n";
        help += GREEN + "list ";
        help += GREY + "- lists all the games\n";
        help += GREEN + "join <ID> [WHITE|BLACK]";
        help += GREY + "- joins a game as the specified color\n";
        help += GREEN + "observe <ID> ";
        help += GREY + "- joins a game as an observer\n";
        help += GREEN + "logout ";
        help += GREY + "- logs you out of chess\n";
        help += GREEN + "help ";
        help += GREY + "- possible commands\n";

        return help;
    }

    private String quit(){
        return "quit";
    }

    private String logout(String authToken){
        String result = serverFacade.logoutUser(authToken);
        if(result.startsWith("failure: ")){
            return RED + result;
        }
        state = State.LOGGED_OUT;
        return result;
    }

    private String createGame(String[] commandArray, String authToken) {
        String result = serverFacade.createGame(commandArray[1], authToken);
        if(result.startsWith("failure: ")){
            return RED + result;
        }
        return result;
    }

    private String listGames(String authToken) {
        String games = serverFacade.listGames(authToken);
        if(games.startsWith("failure: ")){
            return RED + games;
        }
        return games;
    }

    @Override
    public String eval(String line) {
        state = State.LOGGED_IN;
        String[] commandArray = line.split("\\s+");

        if(commandArray[0].equals("quit")){
            return quit();
        }
        else if(commandArray[0].equals("logout")){
            return logout(notificationHandler.getAuthToken());
        }
        else if(commandArray[0].equals("list")){
            return listGames(notificationHandler.getAuthToken());
        }
        else if(commandArray[0].equals("create")){
            if(commandArray.length>2){
                return RED + "too many arguments <3.\nhere are the allowed commands\n" + help();
            }
            if(commandArray.length<2){
                return RED + "you need a game name as well\nhere are the allowed commands\n" + help();
            }
            return createGame(commandArray, notificationHandler.getAuthToken());
        }
        return "";
    }
}
