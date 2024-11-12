package ui;

import java.util.Scanner;

public class Repl {
    private final PreloginClient preloginClient;
    private Client client;
    private final String BLUE = EscapeSequences.SET_TEXT_COLOR_BLUE;
    private final String GREEN = EscapeSequences.SET_TEXT_COLOR_GREEN;
    private final String RESET = EscapeSequences.RESET_TEXT_COLOR;

    public Repl(String serverUrl) {
        preloginClient = new PreloginClient(serverUrl, this);
        client = preloginClient;
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
                result = client.eval(line);
                System.out.print(BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + RESET + client.state +">>> " + GREEN);
    }

}
