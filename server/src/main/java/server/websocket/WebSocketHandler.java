package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
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
            case LEAVE -> leave(userGameCommand.getAuthToken());
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
        //how can I find out what game ID they are trying to join
        connections.add(gameID, connection);
        String message;
        //find a way to figure out if the person is connecting to observe or to play and what color they are playing as

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

    private void leave(String authString) {

    }

}
