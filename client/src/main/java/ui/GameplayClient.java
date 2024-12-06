package ui;

import chess.*;
import serverfacade.ServerFacade;
import serverfacade.WebsocketFacade;

import java.util.Objects;

public class GameplayClient implements Client{
    private final ServerFacade serverFacade;
    private String visitorName = null;
    private final String serverUrl;
    private final Repl repl;
    private State state = State.LOGGED_IN;
    private ChessGame chessGame = null;// I might change this to just a string of the board later
    private WebsocketFacade websocketFacade;
    private final String red = EscapeSequences.SET_TEXT_COLOR_RED;
    private DisplayBoard displayBoard = new DisplayBoard();

    public GameplayClient(String serverUrl, Repl repl, ServerFacade serverFacade) throws Exception {
        this.serverUrl = serverUrl;
        this.repl = repl;
        this.serverFacade = serverFacade;
        try {
            websocketFacade = new WebsocketFacade(serverUrl, repl);
            websocketFacade.connect(repl.getAuthToken(), repl.joinedGameID);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public State getState() {
        return state;
    }

    @Override
    public String help() {
        String green = EscapeSequences.SET_TEXT_COLOR_GREEN;
        String grey = EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY;
        String help = green + "\nhelp";
        help += grey + "- possible commands\n";
        help += green + "redraw";
        help += grey + "- reprints the board\n";
        help += green + "leave";
        help += grey + "- exit the game. The game will still remain active" +
                "\n if you are a player, any player can enter the game as the position you were playing\n";
        help += green + "move <starting column letter><starting row number> <ending column letter><ending row number> <promotion piece type>";
        help += grey + "- moves piece located in the starting position to the ending position\n" +
                "promotion piece type is only needed if the piece is a pawn that is about to be promoted\n" +
                "promotion piece MUST be formated like so:\n" +
                "        KING,\n" +
                "        QUEEN,\n" +
                "        BISHOP,\n" +
                "        KNIGHT,\n" +
                "        ROOK,\n" +
                "        PAWN\n";
        help += green + "resign";
        help += grey + "- forfeight the game. You can still look at the board,\nbut you can no longer make moves.";
        help += green + "\nhighlight <piece position column letter><piece position row number>";
        help += grey + "- highlights the legal moves of the piece at the position you provide\n";
        return help;
    }

    private String redraw() {
        if(repl.joinedChessGame != null){
            if(Objects.equals(repl.userColor, "BLACK")) {
                return displayBoard.printGameBlack(repl.joinedChessGame.getBoard());
            }
            else{
                return displayBoard.printGameWhite(repl.joinedChessGame.getBoard());
            }
        } else{
            return red + "You currently are not in a chessGame";
        }
    }

    private String leave(String[] commandArray, String authToken){
        try{
            websocketFacade.leave(authToken, repl.joinedGameID);
            state = State.LOGGED_IN;
        } catch (Exception e) {
            return red + "Something went wrong on our end.";
        }
        return "";
    }

    private String makeMove(String[] commandArray, String authToken) {
        ChessPosition startPosition = positionProcessor(commandArray[1]);
        ChessPosition endPosition = positionProcessor(commandArray[2]);
        ChessPiece.PieceType promotionPiece = null;
        if(commandArray.length == 4){
            try{
                promotionPiece = ChessPiece.PieceType.valueOf(commandArray[3]);
            } catch (Exception e) {
                return red + "your promotion piece was not in the valid formatting";
            }
        }
        try{
            websocketFacade.makeMove(authToken, repl.joinedGameID, new ChessMove(startPosition, endPosition, promotionPiece));
        }catch (Exception e) {
            return red + "Something went wrong on our end.";
        }
        return "";
    }

    private String resign(String[] commandArray, String authToken) {
        try{
            websocketFacade.resign(authToken, repl.joinedGameID);
        } catch (Exception e) {
            return red + "Something went wrong on our end.";
        }
        return "";
    }

    private String highlight() {
        return "";
    }

    private ChessPosition positionProcessor(String chessPositionStr){
        int col = chessPositionStr.charAt(0) - 'a' + 1;
        int row = Character.getNumericValue(chessPositionStr.charAt(1));
        return new ChessPosition(row, col);
    }

    @Override
    public String eval(String line) {
        state = State.IN_GAME;
        String[] commandArray = line.split("\\s+");

        if(commandArray[0].equals("help")){
            return help();
        }
        else if(commandArray[0].equals("redraw")) {
            if (commandArray.length > 1) {
                return red + "too many arguments <3.\nhere are the allowed commands\n" + help();
            }
            return redraw();
        }
        else if(commandArray[0].equals("leave")) {
            if (commandArray.length > 1) {
                return red + "too many arguments <3.\nhere are the allowed commands\n" + help();
            }
            return leave(commandArray, repl.getAuthToken());
        }
        else if(commandArray[0].equals("move")) {
            if (commandArray.length > 4) {
                return red + "too many arguments <3.\nhere are the allowed commands\n" + help();
            }
            if (commandArray.length < 3) {
                return red + "not enough arguments\nhere are the allowed commands\n" + help();
            }
            return makeMove(commandArray, repl.getAuthToken());
        }
        else if(commandArray[0].equals("resign")) {
            if (commandArray.length > 1) {
                return red + "too many arguments <3.\nhere are the allowed commands\n" + help();
            }
            return resign(commandArray, repl.getAuthToken());
        }
        else if(commandArray[0].equals("highlight")) {
            if (commandArray.length > 2) {
                return red + "too many arguments <3.\nhere are the allowed commands\n" + help();
            }
            if (commandArray.length < 2) {
                return red + "not enough arguments\nhere are the allowed commands\n" + help();
            }
            return highlight();
        }
        return red + "Please enter a valid command\n"+help();
    }
}
