package client;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.*;
import server.Server;
import serverfacade.ServerFacade;
import serverfacade.ServerFacadeListGamesReturn;

import java.util.ArrayList;
import java.util.List;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;
    private static List<GameData> gamesList = new ArrayList<>();
    private static int gameID;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade("http://localhost:"+port);
    }

    @BeforeEach
    public void setUp() {
        serverFacade.clear();
        String auth = serverFacade.registerUser("Elise", "password", "email");
        gamesList = new ArrayList<>();
        String gameIDStr = serverFacade.createGame("game1", auth);
        gameID = Integer.parseInt(gameIDStr);
        gamesList.add(new GameData(gameID, null, null, "game1", new ChessGame()));
    }

    @AfterAll
    static void stopServer() {
        server.stop();
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

    @Test
    public void joinGameTest() {
        String auth = serverFacade.loginUser("Elise", "password");
        System.out.println(auth);
        Assertions.assertNotNull(auth);
        Assertions.assertFalse(auth.startsWith("failure: "));
        ServerFacadeListGamesReturn responseAndGamesList = serverFacade.listGames(auth);
        Assertions.assertFalse(responseAndGamesList.response().startsWith("failure: "));
        Assertions.assertEquals(gamesList, responseAndGamesList.gamesList());
        String response = serverFacade.joinGame("WHITE", gameID, auth);
        Assertions.assertFalse(response.startsWith("failure: "));
    }

    @Test
    public void joinGameFailureTest() {
        String auth = serverFacade.loginUser("Elise", "password");
        System.out.println(auth);
        Assertions.assertNotNull(auth);
        Assertions.assertFalse(auth.startsWith("failure: "));
        ServerFacadeListGamesReturn responseAndGamesList = serverFacade.listGames(auth);
        Assertions.assertFalse(responseAndGamesList.response().startsWith("failure: "));
        Assertions.assertEquals(gamesList, responseAndGamesList.gamesList());
        String response = serverFacade.joinGame("WHITE", gameID, auth);
        Assertions.assertFalse(response.startsWith("failure: "));
        response = serverFacade.joinGame("WHITE", gameID, auth);
        Assertions.assertTrue(response.startsWith("failure: "));
    }

    @Test
    public void listGameFailureTest() {
        String auth = serverFacade.loginUser("Elise", "password");
        System.out.println(auth);
        Assertions.assertNotNull(auth);
        Assertions.assertFalse(auth.startsWith("failure: "));
        ServerFacadeListGamesReturn responseAndGamesList = serverFacade.listGames("");
        Assertions.assertTrue(responseAndGamesList.response().startsWith("failure: "));
    }

    @Test
    public void registerUserTest() {
        String auth = serverFacade.registerUser("username", "password", "email");
        System.out.println(auth);
        Assertions.assertNotNull(auth);
        Assertions.assertFalse(auth.startsWith("failure: "));
    }

    @Test
    public void registerUserFailureTest() {
        String auth = serverFacade.registerUser("username", null, "email");
        System.out.println(auth);
        Assertions.assertNotNull(auth);
        Assertions.assertTrue(auth.startsWith("failure: "));
    }

    @Test
    public void loginUserTest() {
        String auth = serverFacade.loginUser("Elise", "password");
        System.out.println(auth);
        Assertions.assertNotNull(auth);
        Assertions.assertFalse(auth.startsWith("failure: "));
    }

    @Test
    public void loginUserFailureTest() {
        String auth = serverFacade.loginUser("Elise", "wrongPassword");
        System.out.println(auth);
        Assertions.assertNotNull(auth);
        Assertions.assertTrue(auth.startsWith("failure: "));
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
    public void logoutUserFailureTest() {
        String auth = serverFacade.loginUser("Elise", "password");
        System.out.println(auth);
        Assertions.assertNotNull(auth);
        Assertions.assertFalse(auth.startsWith("failure: "));
        String result = serverFacade.logoutUser("");
        Assertions.assertTrue(result.startsWith("failure: "));
    }

    @Test
    public void clearTest() {
        String response = serverFacade.clear();
        Assertions.assertFalse(response.startsWith("failure: "));
    }
}
