package serverfacade;

import com.google.gson.Gson;
import ui.Repl;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebsocketFacade {
    //send a request to set up the connection with the server directly
    ///wslocal host stuff
    //have something that saves the session variable in the chessboard ui
    //create a websocket client and call the send method on it
    Session session;
    Repl repl;


    public WebsocketFacade(String url, Repl repl) throws Exception {
        this.repl = repl;
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    repl.notify(serverMessage);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new Exception(ex.getMessage());
        }
    }

    public void connect(String authToken, int gameID) throws Exception {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID); //tells the server that someone just came in. action class is something that Prof wrote
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new Exception(ex.getMessage());
        }
    }

    public void makeMove(String authToken, int gameID) throws Exception {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID); //tells the server that someone just came in. action class is something that Prof wrote
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new Exception(ex.getMessage());
        }
    }

    public void leave(String authToken, int gameID) throws Exception {
        try {
            var action = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
            this.session.close();
        } catch (IOException ex) {
            throw new Exception(ex.getMessage());
        }
    }

    public void resign(String authToken, int gameID) throws Exception {
        try {
            var action = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);;
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
            this.session.close();
        } catch (IOException ex) {
            throw new Exception(ex.getMessage());
        }
    }
}
