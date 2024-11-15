package ui;

import chess.ChessBoard;
import serverfacade.ServerFacade;

import java.util.Scanner;

import static ui.State.*;

public class Repl {
    private final PreloginClient preloginClient;
    private final PostloginClient postloginClient;
    private final GameplayClient gameplayClient;
    private Client client;
    private String authToken;
    ServerFacade serverFacade;
    int joinedGameID;
    DisplayBoard displayBoard = new DisplayBoard();

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

    private void printPrompt() {
        String green = EscapeSequences.SET_TEXT_COLOR_GREEN;
        String reset = EscapeSequences.RESET_TEXT_COLOR;
        System.out.print("\n" + reset +">>> " + green);
    }

}
