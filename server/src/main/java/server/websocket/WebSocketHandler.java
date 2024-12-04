package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

import static java.lang.System.exit;
@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();
    UserDAO userDataAccess;
    AuthDAO authDataAccess;
    GameDAO gameDataAccess;

    public WebSocketHandler(UserDAO userDataAccess,  AuthDAO authDataAccess, GameDAO gameDataAccess ) {
        this.gameDataAccess = gameDataAccess;
        this.authDataAccess = authDataAccess;
        this.userDataAccess = userDataAccess;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, UnauthorizedException, DataAccessException, BadRequestException {
        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        switch (userGameCommand.getCommandType()) {
            case CONNECT -> connect(userGameCommand.getAuthToken(), session, userGameCommand.getGameID()); //track the vistor's name. key your hashmap in your connection pool based on the user's name
            case MAKE_MOVE ->{
                MakeMoveCommand makeMoveCommand = new Gson().fromJson(message, MakeMoveCommand.class);
                makeMove(userGameCommand.getAuthToken(), session, userGameCommand.getGameID(), makeMoveCommand.getMove());}
            case LEAVE -> leave(userGameCommand.getAuthToken(), session, userGameCommand.getGameID());
            case RESIGN -> resign(userGameCommand.getAuthToken(), session, userGameCommand.getGameID());
        }
    }

    private void connect(String authToken, Session session, Integer gameID) throws DataAccessException, UnauthorizedException, IOException, BadRequestException {
        var connection = new Connection(authToken, session);
        AuthData authData = authDataAccess.getAuthData(authToken);
        //how do I send back error messages if my connections requires the username?
        if(authData==null){
            ErrorMessage errorNotification = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "you are not authorized to connect. Please properly login");
            connection.send(errorNotification); // should I add a username to the
        }
        GameData gameData = gameDataAccess.getGame(gameID);
        if(gameData == null){
            ErrorMessage errorNotification = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "that game doesn't exist in our system");
            connection.send(errorNotification);
        }
        connections.add(gameID, connection);
        String message;
        if(authData.username().equals(gameData.whiteUsername())) {
            message = String.format("%s joined the game as white", authData.username());
        }else if(authData.username().equals(gameData.blackUsername())){
            message = String.format("%s joined the game as an black", authData.username());
        }else{
            message = String.format("%s joined the game (as an observer)", authData.username());
        }
        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.send_not_user(gameID, authToken, notification);
        Gson g = new Gson();
        var userNotification = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameData.game());
        connections.send_user(gameID, authToken, userNotification);
    }

    private void makeMove(String authToken, Session session, Integer gameID, ChessMove move) throws IOException, DataAccessException {
        var connection = new Connection(authToken, session);
        AuthData authData = authDataAccess.getAuthData(authToken);
        //how do I send back error messages if my connections requires the username?
        if(authData==null){
            ErrorMessage errorNotification = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "you are not authorized to connect. Please properly login");
            connection.send(errorNotification); // should I add a username to the
        }
        GameData gameData = gameDataAccess.getGame(gameID);
        if(gameData == null){
            ErrorMessage errorNotification = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "that game doesn't exist in our system");
            connection.send(errorNotification);
        }
        String playerColor = "";
        if(authData.username().equals(gameData.whiteUsername())) {
            playerColor = "WHITE";
        }else if(authData.username().equals(gameData.blackUsername())){
            playerColor = "BLACK";
        }else{
            ErrorMessage errorNotification = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "You are not a player in this chess game");
            connection.send(errorNotification);
        }
        if(!gameData.game().getTeamTurn().equals(playerColor)){
            ErrorMessage errorNotification = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "It is not your turn to play");
            connection.send(errorNotification);
        }
        try {
            gameData.game().makeMove(move);
        } catch (InvalidMoveException e) {
            ErrorMessage errorNotification = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, String.format("%s is not a valid move", move.toString()));
            connection.send(errorNotification);
        }

    }

    private void leave(String authToken, Session session, Integer gameID) {
    }

    private void resign(String authToken, Session session, Integer gameID) {
    }
}
