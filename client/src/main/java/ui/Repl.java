package ui;

import chess.ChessBoard;
import chess.ChessGame;
import com.google.gson.Gson;
import serverfacade.ServerFacade;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Objects;
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
        this.serverUrl = serverUrl;

        client = preloginClient;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void run() {
        System.out.println("Welcome to 240 chess. Type Help to get started.");
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in); // Accepts input every enter
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                String[] commandArray = line.split("\\s+");
                result = client.eval(line);
                processCommand(commandArray, result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void processCommand(String[] commandArray, String result) throws Exception {
        switch (commandArray[0]) {
            case "login", "register" -> handleLoginOrRegister(result);
            case "join" -> handleJoin(commandArray, result);
            case "observe" -> handleObserve(result);
            case "leave" -> handleLeave(result);
            case "logout" -> handleLogout(result);
            default -> System.out.print(result + "\n");
        }
    }

    private void handleLoginOrRegister(String result) {
        if (client.getState() == LOGGED_IN) {
            System.out.println("You have been successfully logged in!\n");
            authToken = result;
            client = postloginClient;
            System.out.print(client.help());
        } else {
            System.out.print(result);
        }
    }

    private void handleJoin(String[] commandArray, String result) throws Exception {
        if (client.getState() == IN_GAME) {
            userColor = commandArray[2];
            joinedGameID = postloginClient.joinedGameID;
            client = new GameplayClient(serverUrl, this, serverFacade);
            System.out.print(client.help());
            joinedChessBoardStr = result;
            System.out.print(result);
        } else {
            System.out.print(result);
        }
    }

    private void handleObserve(String result) throws Exception {
        if (client.getState() == IN_GAME) {
            userColor = "observer";
            joinedGameID = postloginClient.joinedGameID;
            client = new GameplayClient(serverUrl, this, serverFacade);
            System.out.print(client.help());
            joinedChessBoardStr = result;
            System.out.print(result);
        } else {
            System.out.print(result);
        }
    }

    private void handleLeave(String result) {
        if (client.getState() == LOGGED_IN) {
            userColor = null;
            client = postloginClient;
            System.out.print(client.help());
            joinedChessGame = null;
            joinedGameID = null;
            System.out.print(result);
        } else {
            System.out.print(result);
        }
    }

    private void handleLogout(String result) {
        if (client.getState() == LOGGED_OUT) {
            client = preloginClient;
        }
        System.out.print(result + "\n");
    }

    public void notify(String message) {
        try {
            ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
            processServerMessage(serverMessage, message);
        } catch (Exception ex) {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + message);
        }
        printPrompt();
    }

    private void processServerMessage(ServerMessage serverMessage, String message) {
        switch (serverMessage.getServerMessageType()) {
            case LOAD_GAME -> handleLoadGame(message);
            case NOTIFICATION -> handleNotification(message);
            case ERROR -> handleError(message);
            default -> System.out.println("Unhandled message type: " + serverMessage.getServerMessageType());
        }
    }

    private void handleLoadGame(String message) {
        LoadGameMessage loadGameMessage = new Gson().fromJson(message, LoadGameMessage.class);
        joinedChessGame = loadGameMessage.getGame();
        if (joinedChessGame != null) {
            System.out.print(Objects.equals(userColor, "BLACK")
                    ? displayBoard.printGameBlack(joinedChessGame.getBoard())
                    : displayBoard.printGameWhite(joinedChessGame.getBoard()));
        }
    }

    private void handleNotification(String message) {
        NotificationMessage notificationMessage = new Gson().fromJson(message, NotificationMessage.class);
        System.out.print(notificationMessage.getMessage());
    }

    private void handleError(String message) {
        ErrorMessage errorMessage = new Gson().fromJson(message, ErrorMessage.class);
        System.out.print(errorMessage.getErrorMessage());
    }


    public void printPrompt() {
        String green = EscapeSequences.SET_TEXT_COLOR_GREEN;
        String reset = EscapeSequences.RESET_TEXT_COLOR;
        System.out.print("\n" + reset +">>> " + green);
    }

}
