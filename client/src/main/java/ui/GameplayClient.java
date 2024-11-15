package ui;

import chess.ChessBoard;
import chess.ChessGame;
import serverfacade.ServerFacade;

public class GameplayClient implements Client{
    private final ServerFacade serverFacade;
    private String visitorName = null;
    private final String serverUrl;
    private final Repl notificationHandler;
    private State state = State.LOGGED_OUT;
    private ChessGame chessGame = null; // I might change this to just a string of the board later

    public GameplayClient(String serverUrl, Repl notificationHandler, ServerFacade serverFacade) {
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
        return "";
    }

    @Override
    public String eval(String line) {
        return "";
    }
}
