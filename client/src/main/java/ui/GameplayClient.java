package ui;

import chess.ChessBoard;
import chess.ChessGame;

public class GameplayClient implements Client{
    private String visitorName = null;
    private final String serverUrl;
    private final Repl notificationHandler;
    private State state = State.LOGGED_OUT;
    private ChessGame chessGame = null; // I might change this to just a string of the board later
    private final String BLUE = EscapeSequences.SET_TEXT_COLOR_BLUE;
    private final String GREY = EscapeSequences.SET_TEXT_COLOR_DARK_GREY;

    public GameplayClient(String serverUrl, Repl notificationHandler) {
        this.serverUrl = serverUrl;
        this.notificationHandler = notificationHandler;
    }

    @Override
    public String help() {
        return "";
    }

    @Override
    public String eval(String line) {
        return "";
    }

    private String printBoard(ChessBoard board){
        return "";
    }
}
