package websocket.commands;

public class ResignCommand extends UserGameCommand{
    public ResignCommand(String authToken) {
        super(authToken);
        commandType = CommandType.RESIGN;
    }
}
