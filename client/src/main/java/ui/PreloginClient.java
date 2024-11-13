package ui;

//import com.sun.nio.sctp.NotificationHandler;

import serverFacade.ServerFacade;

public class PreloginClient implements Client{
    private String visitorName = null;
    private final String serverUrl;
    private final Repl notificationHandler;
    ServerFacade serverFacade;

    private State state = State.LOGGED_OUT;
    private final String BLUE = EscapeSequences.SET_TEXT_COLOR_BLUE;
    private final String RED = EscapeSequences.SET_TEXT_COLOR_RED;
    private final String GREY = EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY;


    public PreloginClient(String serverUrl, Repl notificationHandler, ServerFacade serverFacade) {
        this.serverUrl = serverUrl;
        this.notificationHandler = notificationHandler;
        this.serverFacade = serverFacade;
    }


    @Override
    public State getState() {
        return state;
    }

    @Override
    public String help() {
        //add new lines
        String help = BLUE + "register <USERNAME> <PASSWORD> <EMAIL> ";
        help += GREY + "- creates a new account\n";
        help += BLUE + "login <USERNAME> <PASSWORD> ";
        help += GREY + "- to play chess\n";
        help += BLUE + "quit ";
        help += GREY + "- playing chess\n";
        help += BLUE + "help ";
        help += GREY + "- shows you the possible commands\n";

        return help;
    }

    private String register(String[] commandArray){
        String registerResult = serverFacade.registerUser(commandArray[1],commandArray[2],commandArray[3]);
        if(registerResult.startsWith("failure: ")){
            return RED + registerResult;
        }
        state = State.LOGGED_IN;
        return registerResult;
    }

    private String login(String[] commandArray){
        String result = serverFacade.loginUser(commandArray[1],commandArray[2]);
        if(result.startsWith("failure: ")){
            return RED + result;
        }
        state = State.LOGGED_IN;
        return result;
    }

    private String quit(){
        return "quit";
    }

    @Override
    public String eval(String line) {
        state = State.LOGGED_OUT;
        String[] commandArray = line.split("\\s+");
        if(commandArray[0].equals("help")){
            if(commandArray.length>1){
                return RED + "you technically put in too many arguments, but I'm gonna let it slide because you're such a cutie\n" + help();
            }
            return help();
        }
        else if(commandArray[0].equals("quit")){
            return quit();
        }
        else if(commandArray[0].equals("login")){
            if(commandArray.length>3){
                return RED + "too many arguments <3.\nhere are the allowed commands\n" + help();
            }
            if(commandArray.length<3){
                return RED + "not enough arguments <3.\nhere are the allowed commands\n" + help();
            }
            return login(commandArray);
        }
        else if(commandArray[0].equals("register")){
            if(commandArray.length>4){
                return RED + "too many arguments <3.\nhere are the allowed commands\n" + help();
            }
            if(commandArray.length<4){
                return RED + "not enough arguments <3.\nhere are the allowed commands\n" + help();
            }
            return register(commandArray);
        }
        return RED + "that isn not an allowed command.\nhere are the allowed commands:\n" + help();
    }
}
