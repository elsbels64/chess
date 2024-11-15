package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameData;
import serverFacade.ServerFacade;
import serverFacade.ServerFacadeListGamesReturn;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PostloginClient implements Client{
    private final String serverUrl;
    private final Repl notificationHandler;
    private ServerFacade serverFacade;
    private State state = State.LOGGED_IN;
    private final String GREEN = EscapeSequences.SET_TEXT_COLOR_GREEN;
    private final String GREY = EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY;
    private final String RED = EscapeSequences.SET_TEXT_COLOR_RED;
    private final String WHITE = EscapeSequences.SET_TEXT_COLOR_WHITE;
    private final String BLACK = EscapeSequences.SET_TEXT_COLOR_BLACK;
    private List<Integer> gameIDs = new ArrayList<>();
    private List<ChessGame> chessGamesList = new ArrayList<>();
    public int joinedGameID = 0;

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
        return "successfully created new game: " + commandArray[1];
    }

    private String listGames(String authToken) {
        ServerFacadeListGamesReturn response = serverFacade.listGames(authToken);
        if(response.response().startsWith("failure: ")){
            return RED + response.response();
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
                return printGameBlack(game.getBoard()) + "\n\n\n" + printGameWhite(game.getBoard());
            }catch(NumberFormatException ex){
                return RED + "gameID needs to be an integer\n" + commandArray[1] + " is not an integer"+
                        "\nCall list games to see the available game numbers\n" +
                        "then enter the list number that is next to the name of the game you want to join\n";
            }
        }catch(IndexOutOfBoundsException ex){
            return RED + "we do not have a game #" + commandArray[1] + "\nCall list games to see the available game numbers\n";
        }
    }

    final static Map<ChessPiece.PieceType, String> TYPE_TO_CHAR_MAP = Map.of(
        ChessPiece.PieceType.PAWN, " ♟ ",
        ChessPiece.PieceType.KNIGHT, " ♞ ",
        ChessPiece.PieceType.BISHOP, " ♝ ",
        ChessPiece.PieceType.ROOK, " ♜ ",
        ChessPiece.PieceType.QUEEN," ♛ ",
        ChessPiece.PieceType.KING, " ♚ "
    );

    private void setSpace(ChessBoard board, StringBuilder boardString, int row, int col) {
        List<String> backgroundColors = new ArrayList<>();
        backgroundColors.add(EscapeSequences.SET_BG_COLOR_LIGHT_BROWN);
        backgroundColors.add(EscapeSequences.SET_BG_COLOR_BROWN);
        boardString.append(backgroundColors.get((row+col)%2));
        if (board.getPiece(new ChessPosition(row, col)) == null) {
            boardString.append("\u2003 \u202f\u202f");
        } else {
            String color = board.getPiece(new ChessPosition(row, col)).getTeamColor() == ChessGame.TeamColor.WHITE
                    ? WHITE
                    : BLACK;
            boardString.append(color);
            boardString.append(TYPE_TO_CHAR_MAP.get(board.getPiece(new ChessPosition(row, col)).getPieceType()));
        }
    }

    public String printGameBlack(ChessBoard board){
        StringBuilder boardString = new StringBuilder();
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                setSpace(board, boardString, row, col);
            }
            boardString.append(EscapeSequences.RESET_BG_COLOR);
            boardString.append("\n");
        }
        return String.valueOf(boardString);
    }

    public String printGameWhite(ChessBoard board){
        StringBuilder boardString = new StringBuilder();
        for (int row = 8; row >= 1; row--) {
            for (int col = 8; col >= 1; col--) {
                setSpace(board, boardString, row, col);
            }
            boardString.append(EscapeSequences.RESET_BG_COLOR);
            boardString.append("\n");
        }
        return String.valueOf(boardString);
    }

    private String observeGame(String[] commandArray, String authToken) {
        try {
            try{
            int gameIDInteger = Integer.parseInt(commandArray[1])-1;
            int gameID = gameIDs.get(gameIDInteger);
            ChessGame game = chessGamesList.get(gameIDInteger);
//            state = State.IN_GAME; // this switches the state to IN_GAME which makes it possible for the repl to switch to the gameplay client
            joinedGameID = gameID;
            return "observing game #" + commandArray[1] + "\n" + printGameBlack(game.getBoard()) + "\n\n\n" + printGameWhite(game.getBoard());
            }catch(NumberFormatException ex){
                return RED + "gameID needs to be an integer\n" + commandArray[1] + " is not an integer"+
                        "\nCall list games to see the available game numbers\n" +
                        "then enter the list number that is next to the name of the game you want to join\n";
            }
        }catch(IndexOutOfBoundsException ex){
            return RED + "we do not have a game #" + commandArray[1] + "\nCall list games to see the available game numbers\n";
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
                return RED + "too many arguments <3.\nhere are the allowed commands\n" + help();
            }
            if(commandArray.length<2){
                return RED + "you need a game name as well\nhere are the allowed commands\n" + help();
            }
            return createGame(commandArray, notificationHandler.getAuthToken());
        }
        else if(commandArray[0].equals("join")){
            if(commandArray.length>3){
                return RED + "too many arguments <3.\nhere are the allowed commands\n" + help();
            }
            if(commandArray.length<3){
                return RED + "not enough arguments\nhere are the allowed commands\n" + help();
            }
            return joinGame(commandArray, notificationHandler.getAuthToken());
        }
        else if(commandArray[0].equals("observe")){
            if(commandArray.length>2){
                return RED + "too many arguments <3.\nhere are the allowed commands\n" + help();
            }
            if(commandArray.length<2){
                return RED + "not enough arguments\nhere are the allowed commands\n" + help();
            }
            return observeGame(commandArray, notificationHandler.getAuthToken());
        }
        return RED + "Please enter a valid command\n"+help();
    }
}
