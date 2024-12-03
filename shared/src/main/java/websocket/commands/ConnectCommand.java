package websocket.commands;

public class ConnectCommand extends UserGameCommand {

    public ConnectCommand(String authToken) {
        super(authToken);
        commandType = CommandType.CONNECT;
    }
}
