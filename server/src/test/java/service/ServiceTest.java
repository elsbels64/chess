package service;

import dataaccess.*;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServiceTest {

    MemoryUserDAO userDataAccess;
    MemoryAuthDAO authDataAccess;
    MemoryGameDAO gameDataAccess;
    private Service service;

    // Initialize fresh instances of DAOs and Service before each test
    @BeforeEach
    public void setUp() {
        userDataAccess = new MemoryUserDAO();
        authDataAccess = new MemoryAuthDAO();
        gameDataAccess = new MemoryGameDAO();
        service = new Service(userDataAccess, authDataAccess, gameDataAccess);
    }
    @Test
    public void registerUserFailure() throws BadRequestException, AlreadyTakenException {
        var userData = new UserData("userName", "Password", "email@email.com");
        service.registerUser(userData);

        Assertions.assertThrows(AlreadyTakenException.class, () -> service.registerUser(userData));
    }
    @Test
    public void registerUserSuccess() throws BadRequestException, AlreadyTakenException {
        var userData = new UserData("newUser", "Password", "newUser@email.com");
        var authData = service.registerUser(userData);
        Assertions.assertNotNull(authData);  // Verify that authData is returned
        Assertions.assertEquals("newUser", authData.username());  // Verify correct username
    }

    @Test
    public void logoutUserSuccess() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException {
        var userData = new UserData("newUser", "Password", "newUser@email.com");
        var authData = service.registerUser(userData);

        Assertions.assertDoesNotThrow(() -> service.logoutUser(authData.authToken()));
    }

    @Test
    public void logoutUserFailure() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException {
        var userData = new UserData("newUser", "Password", "newUser@email.com");

        Assertions.assertThrows(UnauthorizedException.class,() -> service.logoutUser("fakeAuthString"));
    }

    @Test
    public void loginUserSuccess() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException {
        var userData = new UserData("newUser", "Password", "newUser@email.com");
        var authData = service.registerUser(userData);
        Assertions.assertDoesNotThrow(() -> service.logoutUser(authData.authToken()));
        var newAuthData = service.loginUser(userData);
        Assertions.assertNotNull(authData);  // Verify that authData is returned
        Assertions.assertEquals("newUser", authData.username());
    }

    @Test
    public void loginUserFailure() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException {
        var userData = new UserData("newUser", "Password", "newUser@email.com");

        Assertions.assertThrows(UnauthorizedException.class,() -> service.loginUser(userData));
    }

    @Test
    public void loginUserFailureBadPassword() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException {
        var userData = new UserData("newUser", "Password", "newUser@email.com");
        var badUserData = new UserData("newUser", "fake password", "newUser@email.com");

        var authData = service.registerUser(userData);
        Assertions.assertDoesNotThrow(() -> service.logoutUser(authData.authToken()));
        Assertions.assertThrows(UnauthorizedException.class,() -> service.loginUser(badUserData));
    }

    @Test
    public void loginUserFailureEmptyData() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException {
        var userData = new UserData("newUser", "Password", "newUser@email.com");
        var badUserData = new UserData("newUser", null, "newUser@email.com");

        var authData = service.registerUser(userData);
        Assertions.assertDoesNotThrow(() -> service.logoutUser(authData.authToken()));
        Assertions.assertThrows(BadRequestException.class,() -> service.loginUser(badUserData));
    }

    @Test
    public void createGameSuccess() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException {
        var userData = new UserData("newUser", "Password", "newUser@email.com");
        var authData = service.registerUser(userData);
        var gameName = "New Game";
        var gameID = service.createGame(authData.authToken(), gameName);
        Assertions.assertNotNull(gameID);
        Assertions.assertDoesNotThrow(()->gameDataAccess.getGame(gameID));
        var game = gameDataAccess.getGame(gameID);
        Assertions.assertEquals("New Game", game.gameName());
        Assertions.assertEquals(null, game.whiteUsername());
        Assertions.assertEquals(null, game.blackUsername());
    }

    @Test
    public void createGameFailure() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException {
        Assertions.assertThrows(UnauthorizedException.class,()->service.createGame("fake auth","game Name"));
    }

    @Test
    public void getGamesSuccesSimple() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException {
        var userData = new UserData("newUser", "Password", "newUser@email.com");
        var authData = service.registerUser(userData);
        var gameName = "New Game";
        var gameID = service.createGame(authData.authToken(), gameName);
        Assertions.assertNotNull(gameID);
        Assertions.assertDoesNotThrow(()->gameDataAccess.getGame(gameID));
        var games = service.getGames(authData.authToken());
        Assertions.assertEquals("New Game", games.get(0).gameName());
        Assertions.assertEquals(null, games.get(0).whiteUsername());
        Assertions.assertEquals(null, games.get(0).blackUsername());
    }

    @Test
    public void getGamesSucces() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException {
        var userData = new UserData("newUser", "Password", "newUser@email.com");
        var authData = service.registerUser(userData);
        var gameName1 = "New Game";
        var gameID = service.createGame(authData.authToken(), gameName1);
        Assertions.assertDoesNotThrow(()->gameDataAccess.getGame(gameID));
        var gameData1 = gameDataAccess.getGame(gameID);
        var gameName2 = "New Game 2";
        var gameID2 = service.createGame(authData.authToken(), gameName2);
        Assertions.assertDoesNotThrow(()->gameDataAccess.getGame(gameID2));
        var gameData2 = gameDataAccess.getGame(gameID2);
        var games = service.getGames(authData.authToken());
        Assertions.assertEquals("New Game", games.get(0).gameName());
        Assertions.assertEquals(null, games.get(0).whiteUsername());
        Assertions.assertEquals(null, games.get(0).blackUsername());
        Assertions.assertEquals(gameData1,games.get(0));
        Assertions.assertEquals("New Game 2", games.get(1).gameName());
        Assertions.assertEquals(null, games.get(1).whiteUsername());
        Assertions.assertEquals(null, games.get(1).blackUsername());
        Assertions.assertEquals(gameData2,games.get(1));
    }

    @Test
    public void getGamesFailure() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException {
        var userData = new UserData("newUser", "Password", "newUser@email.com");
        var authData = service.registerUser(userData);
        var gameName = "New Game";
        var gameID = service.createGame(authData.authToken(), gameName);
        Assertions.assertDoesNotThrow(()->gameDataAccess.getGame(gameID));
        Assertions.assertThrows(UnauthorizedException.class,()->service.getGames("fake auth"));
    }

    @Test
    public void joinGameFailure() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException {
        var userData = new UserData("newUser", "Password", "newUser@email.com");
        var authData = service.registerUser(userData);
        var gameName = "New Game";
        var gameID = service.createGame(authData.authToken(), gameName);
        Assertions.assertDoesNotThrow(()->gameDataAccess.getGame(gameID));
        Assertions.assertThrows(UnauthorizedException.class,
                ()->service.joinGame("fake auth", gameID, "white"));
    }

    @Test
    public void joinGameSucces() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException {
        var userData = new UserData("newUser", "Password", "newUser@email.com");
        var authData = service.registerUser(userData);
        var gameName = "New Game";
        var gameID = service.createGame(authData.authToken(), gameName);
        Assertions.assertNotNull(gameID);
        Assertions.assertDoesNotThrow(()->gameDataAccess.getGame(gameID));
        var games = service.getGames(authData.authToken());
        Assertions.assertEquals("New Game", games.get(0).gameName());
        Assertions.assertEquals(null, games.get(0).whiteUsername());
        Assertions.assertEquals(null, games.get(0).blackUsername());
        service.joinGame(authData.authToken(), gameID, "WHITE");
        games = service.getGames(authData.authToken());
        Assertions.assertEquals("New Game", games.get(0).gameName());
        Assertions.assertEquals("newUser", games.get(0).whiteUsername());
        Assertions.assertEquals(null, games.get(0).blackUsername());
    }

    @Test
    public void joinGameFailureColorAlreadyTaken() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException {
        var userData = new UserData("newUser", "Password", "newUser@email.com");
        var authData = service.registerUser(userData);
        var userData2 = new UserData("newUser2", "Password", "newUser@email.com");
        var authData2 = service.registerUser(userData2);
        var gameName = "New Game";
        var gameID = service.createGame(authData.authToken(), gameName);
        Assertions.assertNotNull(gameID);
        Assertions.assertDoesNotThrow(()->gameDataAccess.getGame(gameID));
        var games = service.getGames(authData.authToken());
        Assertions.assertEquals("New Game", games.get(0).gameName());
        Assertions.assertEquals(null, games.get(0).whiteUsername());
        Assertions.assertEquals(null, games.get(0).blackUsername());
        service.joinGame(authData.authToken(), gameID, "WHITE");
        games = service.getGames(authData.authToken());
        Assertions.assertEquals("New Game", games.get(0).gameName());
        Assertions.assertEquals("newUser", games.get(0).whiteUsername());
        Assertions.assertEquals(null, games.get(0).blackUsername());
        Assertions.assertThrows(AlreadyTakenException.class,
                ()->service.joinGame(authData2.authToken(), gameID, "WHITE"),
                "did not throw Already taken exception");
    }

}
