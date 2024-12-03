package server.websocket;

import com.google.gson.Gson;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;
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
    public void onMessage(Session session, String message) throws IOException, UnauthorizedException, DataAccessException {
        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        switch (userGameCommand.getCommandType()) {
            case CONNECT -> connect(userGameCommand.getAuthString(), session); //track the vistor's name. key your hashmap in your connection pool based on the user's name
            case LEAVE -> leave(userGameCommand.getAuthString());
        }
    }

    private void connect(String authString, Session session) throws DataAccessException, UnauthorizedException, IOException {
        AuthData authData = authDataAccess.getAuthData(authString);
        if(authData==null){
            throw new UnauthorizedException("You do not have the correct authdata");
        }
        //how can I find out what game ID they are trying to join
        connections.add(authData.username(), session);
        String message;
        //find a way to figure out if the person is connecting to observe or to play and what color they are playing as
        String playerColor = "WHITE";
        if(playerColor != null) {
            message = String.format("%s joined the game as %s", authData.username(), playerColor);
        }else{
            message = String.format("%s joined the game as an %s", authData.username(), "observer");
        }
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.send_not_user(authData.username(), notification);
    }

    private void leave(String authString) {

    }

}
