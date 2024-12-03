package websocket.messages;

import chess.ChessGame;

public class LoadGameMessage extends ServerMessage{
    ChessGame game;
    public LoadGameMessage(ServerMessageType type, String message, ChessGame game) {
        super(type, message);
        this.game = game;
    }
}
