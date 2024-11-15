package ui;

import chess.ChessGame;
import model.GameData;
import serverfacade.ServerFacade;
import serverfacade.ServerFacadeListGamesReturn;

import java.util.ArrayList;
import java.util.List;

public class PostloginClient implements Client{
    private final String serverUrl;
    private final Repl notificationHandler;
    private final ServerFacade serverFacade;
    private State state = State.LOGGED_IN;
    private final String red = EscapeSequences.SET_TEXT_COLOR_RED;
    private List<Integer> gameIDs = new ArrayList<>();
    private List<ChessGame> chessGamesList = new ArrayList<>();
    public int joinedGameID = 0;
    private final DisplayBoard displayBoard = new DisplayBoard();

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
        String green = EscapeSequences.SET_TEXT_COLOR_GREEN;
        String help = green + "\ncreate <NAME> ";
        String grey = EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY;
        help += grey + "- creates a new game with the provided game name\n";
        help += green + "list ";
        help += grey + "- lists all the games\n";
        help += green + "join <ID> [WHITE|BLACK]";
        help += grey + "- joins a game as the specified color\n";
        help += green + "observe <ID> ";
        help += grey + "- joins a game as an observer\n";
        help += green + "logout ";
        help += grey + "- logs you out of chess\n";
        help += green + "help ";
        help += grey + "- possible commands\n";

        return help;
    }

    public List<Integer> getGameIDs() {
        return gameIDs;
    }

    public List<ChessGame> getChessGamesList() {
        return chessGamesList;
    }

    private String quit(){
        return "quit";
    }

    private String logout(String authToken){
        String result = serverFacade.logoutUser(authToken);
        if(result.startsWith("failure: ")){
            return red + result;
        }
        state = State.LOGGED_OUT;
        return result;
    }

    private String createGame(String[] commandArray, String authToken) {
        String result = serverFacade.createGame(commandArray[1], authToken);
        if(result.startsWith("failure: ")){
            return red + result;
        }
        return "successfully created new game: " + commandArray[1];
    }

    private String listGames(String authToken) {
        ServerFacadeListGamesReturn response = serverFacade.listGames(authToken);
        if(response.response().startsWith("failure: ")){
            return red + response.response();
        }
        List<GameData> gamesList = response.gamesList();
        StringBuilder gamesStr = new StringBuilder();
        gameIDs = new ArrayList<>();
        chessGamesList = new ArrayList<>();
        for(int i=0; i < gamesList.size(); i++){
            GameData game = gamesList.get(i);
            gameIDs.add(game.gameID());
            chessGamesList.add(game.game());
            gamesStr.append((i + 1)).append(": ").append(game.gameName()).append("\n");
            gamesStr.append("\tWHITE player: ").append(game.whiteUsername()).append("\n");
            gamesStr.append("\tBLACK player: ").append(game.blackUsername()).append("\n");
        }
        return gamesStr.toString();
    }

    private String joinGame(String[] commandArray, String authToken) {
        try {
            try {
            String color = commandArray[2];
                int gameIDInteger = Integer.parseInt(commandArray[1]) - 1;
                int gameID = gameIDs.get(gameIDInteger);
                String response = serverFacade.joinGame(color, gameID, authToken);
                if (response.startsWith("failure: ")) {
                    return response;
                }
                ChessGame game = chessGamesList.get(gameIDInteger);
//                state = State.IN_GAME; // this switches the state to IN_GAME which
                //            makes it possible for the repl to switch to the gameplay client
                joinedGameID = gameID;
                return displayBoard.printGameBlack(game.getBoard()) + "\n\n\n"
                        + displayBoard.printGameWhite(game.getBoard());
            }catch(NumberFormatException ex){
                return red + "gameID needs to be an integer\n" + commandArray[1] + " is not an integer"+
                        "\nCall list games to see the available game numbers\n" +
                        "then enter the list number that is next to the name of the game you want to join\n";
            }
        }catch(IndexOutOfBoundsException ex){
            return red + "we do not have a game #" + commandArray[1] + "\nCall list games to see the available game numbers\n";
        }
    }

    private String observeGame(String[] commandArray, String authToken) {
        try {
            try{
            int gameIDInteger = Integer.parseInt(commandArray[1])-1;
            int gameID = gameIDs.get(gameIDInteger);
            ChessGame game = chessGamesList.get(gameIDInteger);
//            state = State.IN_GAME; // this switches the state to IN_GAME which makes it possible for the repl to switch to the gameplay client
            joinedGameID = gameID;
            return "observing game #" + commandArray[1] + "\n" + displayBoard.printGameBlack(game.getBoard()) +
                    "\n\n\n" + displayBoard.printGameWhite(game.getBoard());
            }catch(NumberFormatException ex){
                return red + "gameID needs to be an integer\n" + commandArray[1] + " is not an integer"+
                        "\nCall list games to see the available game numbers\n" +
                        "then enter the list number that is next to the name of the game you want to join\n";
            }
        }catch(IndexOutOfBoundsException ex){
            return red + "we do not have a game #" + commandArray[1] + "\nCall list games to see the available game numbers\n";
        }
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
                return red + "too many arguments <3.\nhere are the allowed commands\n" + help();
            }
            if(commandArray.length<2){
                return red + "you need a game name as well\nhere are the allowed commands\n" + help();
            }
            return createGame(commandArray, notificationHandler.getAuthToken());
        }
        else if(commandArray[0].equals("join")){
            if(commandArray.length>3){
                return red + "too many arguments <3.\nhere are the allowed commands\n" + help();
            }
            if(commandArray.length<3){
                return red + "not enough arguments\nhere are the allowed commands\n" + help();
            }
            return joinGame(commandArray, notificationHandler.getAuthToken());
        }
        else if(commandArray[0].equals("observe")){
            if(commandArray.length>2){
                return red + "too many arguments <3.\nhere are the allowed commands\n" + help();
            }
            if(commandArray.length<2){
                return red + "not enough arguments\nhere are the allowed commands\n" + help();
            }
            return observeGame(commandArray, notificationHandler.getAuthToken());
        }
        return red + "Please enter a valid command\n"+help();
    }
}
