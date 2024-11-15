package ui;

import chess.ChessBoard;
import chess.ChessGame;
import serverFacade.ServerFacade;

import java.util.Scanner;

import static ui.State.IN_GAME;
import static ui.State.LOGGED_IN;

public class Repl {
    private final PreloginClient preloginClient;
    private final PostloginClient postloginClient;
    private final GameplayClient gameplayClient;
    private Client client;
    private String authToken;
    ServerFacade serverFacade;
    int joinedGameID;

    public Repl(String serverUrl) {
        serverFacade = new ServerFacade(serverUrl);
        preloginClient = new PreloginClient(serverUrl, this, serverFacade);
        postloginClient = new PostloginClient(serverUrl, this, serverFacade);
        gameplayClient = new GameplayClient(serverUrl, this, serverFacade);

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
        System.out.print(postloginClient.printGameBlack(board));
        System.out.print(postloginClient.printGameWhite(board));

        Scanner scanner = new Scanner(System.in); // accepts input every enter
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                String[] commandArray = line.split("\\s+");
                String BLUE = EscapeSequences.SET_TEXT_COLOR_BLUE;
                if(commandArray[0].equals("login")||commandArray[0].equals("register")){
                    result = client.eval(line);
                    if (client.getState() == LOGGED_IN) {
                        System.out.println("you have been successfully logged in!\n");
                        authToken = result;
                        client = postloginClient;
                        System.out.print(client.help());
                    }else{
                        System.out.print(BLUE + result);
                    }
                }
                else if(commandArray[0].equals("join")||commandArray[0].equals("observe")){
                    result = client.eval(line);
                    if (client.getState() == IN_GAME) {
                        System.out.println("you have been successfully logged in!\n");
                        joinedGameID = postloginClient.joinedGameID;
                        client = gameplayClient;
                        System.out.print(client.help());
                    }else{
                        System.out.print(BLUE + result);
                    }
                }
                else {
                    result = client.eval(line);
                    System.out.print(BLUE + result);
                    if (client.getState() == LOGGED_IN) {
                        client = postloginClient;
                    }
                }
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        String GREEN = EscapeSequences.SET_TEXT_COLOR_GREEN;
        String RESET = EscapeSequences.RESET_TEXT_COLOR;
        System.out.print("\n" + RESET + client.getState() +">>> " + GREEN);
    }

}
