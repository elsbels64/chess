package ui;

import chess.ChessBoard;
import chess.ChessGame;
import com.google.gson.Gson;
import serverfacade.ServerFacade;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Scanner;

import static ui.State.*;

public class Repl {
    private final PreloginClient preloginClient;
    private final PostloginClient postloginClient;
    private String serverUrl;
    private Client client;
    private String authToken;
    ServerFacade serverFacade;
    ChessGame joinedChessGame;
    String joinedChessBoardStr = null;
    String userColor;
    Integer joinedGameID;
    DisplayBoard displayBoard = new DisplayBoard();

    public Repl(String serverUrl) {
        serverFacade = new ServerFacade(serverUrl);
        preloginClient = new PreloginClient(serverUrl, this, serverFacade);
        postloginClient = new PostloginClient(serverUrl, this, serverFacade);
//        gameplayClient = new GameplayClient(serverUrl, this, serverFacade);
        this.serverUrl = serverUrl;

        client = preloginClient;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void run() {
        System.out.println("Welcome to 240 chess. Type Help to get started.");
        System.out.print(client.help());

        var board = new ChessBoard();
        board.resetBoard();
        System.out.print(displayBoard.printGameBlack(board));
        System.out.print(displayBoard.printGameWhite(board));

        Scanner scanner = new Scanner(System.in); // accepts input every enter
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                String[] commandArray = line.split("\\s+");
                result = client.eval(line);
                if(commandArray[0].equals("login")||commandArray[0].equals("register")){
                    if(client.getState()==LOGGED_IN){
                        System.out.println("you have been successfully logged in!\n");
                        authToken = result;
                        client = postloginClient;
                        System.out.print(client.help());
                    }else{
                        System.out.print(result);
                    }
                }
                if(commandArray[0].equals("join")){
                    if(client.getState()==IN_GAME){
                        userColor = commandArray[2];
                        joinedGameID = postloginClient.joinedGameID;
                        client = new GameplayClient(serverUrl, this, serverFacade);
                        System.out.print(client.help());
                        joinedChessBoardStr = result;
                        System.out.print(result);
                    }else{
                        System.out.print(result);
                    }
                }
                if(commandArray[0].equals("observe")){
                    if(client.getState()==IN_GAME){
                        userColor = "observer";
                        joinedGameID = postloginClient.joinedGameID;
                        client = new GameplayClient(serverUrl, this, serverFacade);
                        System.out.print(client.help());
                        joinedChessBoardStr = result;
                        System.out.print(result);
                    }else{
                        System.out.print(result);
                    }
                }
                if(commandArray[0].equals("leave")){
                    if(client.getState()==LOGGED_IN){
                        userColor = null;
                        client = postloginClient;
                        System.out.print(client.help());
                        joinedChessGame = null;
                        joinedGameID = null;
                        System.out.print(result);
                    }else{
                        System.out.print(result);
                    }
                }
                else if(commandArray[0].equals("logout")){
                    if (client.getState() == LOGGED_OUT) {
                        client = preloginClient;
                    }
                    System.out.print(result + "\n");
                }else{
                    System.out.print(result + "\n");
                }
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    public void notify(String message) {
        try {
            ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
            switch (serverMessage.getServerMessageType()) {
                case LOAD_GAME -> {
                    LoadGameMessage loadGameMessage = new Gson().fromJson(message, LoadGameMessage.class);
                    // Function to handle this type of message
                    joinedChessGame = loadGameMessage.getGame();
                }
                case NOTIFICATION -> {
                    NotificationMessage notificationMessage = new Gson().fromJson(message, NotificationMessage.class);
                    // Function to handle this type of message (Print this probably)
                }
                case ERROR -> {
                    ErrorMessage errorMessage = new Gson().fromJson(message, ErrorMessage.class);
                    // Function to handle this type of message
                }
                default -> {
                    // Handle unexpected types or provide a default case
                    System.out.println("Unhandled message type: " + serverMessage.getServerMessageType());
                }
            }
        }catch(Exception ex) {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + message);
            printPrompt();
        }
    }

    private void printPrompt() {
        String green = EscapeSequences.SET_TEXT_COLOR_GREEN;
        String reset = EscapeSequences.RESET_TEXT_COLOR;
        System.out.print("\n" + reset +">>> " + green);
    }

}
