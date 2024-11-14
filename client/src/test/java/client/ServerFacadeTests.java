package client;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.*;
import server.Server;
import serverFacade.ServerFacade;
import serverFacade.ServerFacadeListGamesReturn;

import java.util.ArrayList;
import java.util.List;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;
    private static List<GameData> gamesList = new ArrayList<>();

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade("http://localhost:"+port);
        serverFacade.clear();
        String auth = serverFacade.registerUser("Elise", "password", "email");
        String GameIDStr = serverFacade.createGame("game1", auth);
        int gameID = Integer.parseInt(GameIDStr);
        gamesList.add(new GameData(gameID, null, null, "game1", new ChessGame()));
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    public void clearTest() {
        String response = serverFacade.clear();

    }

    @Test
    public void registerUserTest() {
        String auth = serverFacade.registerUser("username", "password", "email");
        System.out.println(auth);
        Assertions.assertNotNull(auth);
        Assertions.assertFalse(auth.startsWith("failure: "));
    }

    @Test
    public void loginUserTest() {
        String auth = serverFacade.loginUser("Elise", "password");
        System.out.println(auth);
        Assertions.assertNotNull(auth);
        Assertions.assertFalse(auth.startsWith("failure: "));
    }

    @Test
    public void logoutUserTest() {
        String auth = serverFacade.loginUser("Elise", "password");
        System.out.println(auth);
        Assertions.assertNotNull(auth);
        Assertions.assertFalse(auth.startsWith("failure: "));
        String result = serverFacade.logoutUser(auth);
        Assertions.assertEquals("You have been successfully logged out", result);
    }

    @Test
    public void listGameTest() {
        String auth = serverFacade.loginUser("Elise", "password");
        System.out.println(auth);
        Assertions.assertNotNull(auth);
        Assertions.assertFalse(auth.startsWith("failure: "));
        ServerFacadeListGamesReturn responseAndGamesList = serverFacade.listGames(auth);
        Assertions.assertFalse(responseAndGamesList.response().startsWith("failure: "));
        Assertions.assertEquals(gamesList, responseAndGamesList.gamesList());
    }

}
