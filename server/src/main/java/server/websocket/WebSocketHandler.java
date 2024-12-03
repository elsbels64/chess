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
import websocket.messages.LoadGameMessage;
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

    private void connect(String authString, Session session, Integer gameID) throws DataAccessException, UnauthorizedException, IOException, BadRequestException {
        AuthData authData = authDataAccess.getAuthData(authString);
        if(authData==null){
            throw new UnauthorizedException("You do not have the correct authdata");
        }
        GameData gameData = gameDataAccess.getGame(gameID);
        if(gameData == null){
            throw new BadRequestException("that game does not exit");
        }
        //how can I find out what game ID they are trying to join
        connections.add(gameID, authData.username(), session);
        String message;
        //find a way to figure out if the person is connecting to observe or to play and what color they are playing as

        if(authData.username().equals(gameData.whiteUsername())) {
            message = String.format("%s joined the game as white", authData.username());
        }else if(authData.username().equals(gameData.blackUsername())){
            message = String.format("%s joined the game as an black", authData.username());
        }else{
            message = String.format("%s joined the game (as an observer)", authData.username());
        }
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.send_not_user(gameID, authData.username(), notification);
        Gson g = new Gson();
        notification = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, "message", gameData.game());
        connections.send_user(gameID, authData.username(), notification);
    }

    private void leave(String authString) {

    }

}
