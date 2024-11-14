package ui;

import serverFacade.ServerFacade;

import java.util.Scanner;

import static ui.State.LOGGED_IN;

public class Repl {
    private final PreloginClient preloginClient;
    private final PostloginClient postloginClient;
    private Client client;
    private String authToken;
    private final String BLUE = EscapeSequences.SET_TEXT_COLOR_BLUE;
    private final String GREEN = EscapeSequences.SET_TEXT_COLOR_GREEN;
    private final String RESET = EscapeSequences.RESET_TEXT_COLOR;
    ServerFacade serverFacade;

    public Repl(String serverUrl) {
        serverFacade = new ServerFacade(serverUrl);
        preloginClient = new PreloginClient(serverUrl, this, serverFacade);
        postloginClient = new PostloginClient(serverUrl, this, serverFacade);
        client = preloginClient;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void run() {
        System.out.println("Welcome to 240 chess. Type Help to get started.");
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in); // accepts input every enter
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                String[] commandArray = line.split("\\s+");
                if(commandArray[0].equals("login")||commandArray[0].equals("register")){
                    result = client.eval(line);
                    if (client.getState() == LOGGED_IN) {
                        System.out.println("you have been successfully logged in!\n");
                        System.out.print(BLUE +"this is the authToken for debugging purposes I will delete this later" + result);
                        authToken = result;
                        client = postloginClient;
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
                        System.out.print(client.help());
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
        System.out.print("\n" + RESET + client.getState() +">>> " + GREEN);
    }

}
