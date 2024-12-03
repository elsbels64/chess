package ui;

import chess.ChessBoard;
import chess.ChessGame;
import serverfacade.ServerFacade;
import serverfacade.WebsocketFacade;

public class GameplayClient implements Client{
    private final ServerFacade serverFacade;
    private String visitorName = null;
    private final String serverUrl;
    private final Repl repl;
    private State state = State.LOGGED_IN;
    private ChessGame chessGame = null;// I might change this to just a string of the board later
    private WebsocketFacade websocketFacade;

    public GameplayClient(String serverUrl, Repl repl, ServerFacade serverFacade) throws Exception {
        this.serverUrl = serverUrl;
        this.repl = repl;
        this.serverFacade = serverFacade;
        websocketFacade = new WebsocketFacade(serverUrl, repl);
        try {
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
        return "";
    }

    @Override
    public String eval(String line) {
        return "";
    }
}
