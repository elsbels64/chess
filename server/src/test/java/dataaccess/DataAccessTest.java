package dataaccess;


import chess.ChessGame;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.UserData;
import service.Service;

import java.util.ArrayList;

public class DataAccessTest {
    UserDAO userDataAccess;
    AuthDAO authDataAccess;
    GameDAO gameDataAccess;

    @BeforeEach
    public void setUp() throws DataAccessException {
        userDataAccess = new MySQLUserDAO();
        authDataAccess = new MySQLAuthDAO();
        gameDataAccess = new MySQLGameDAO();
        userDataAccess.deleteAll();
        authDataAccess.deleteAll();
        gameDataAccess.deleteAll();
    }

    @Test
    public void addUser() throws DataAccessException {
        var expected = new UserData("a", "p", "jkj@gmail.com");
        userDataAccess.addUser(expected);
        var actual = userDataAccess.getUser("a");
        Assertions.assertEquals(expected.username(), actual.username());
        Assertions.assertEquals(expected.email(), actual.email());
    }

    @Test
    public void addInvalidUser() throws DataAccessException {
        var badUser = new UserData(null, "p", "jkj@gmail.com");
        Assertions.assertThrows(DataAccessException.class,() -> userDataAccess.addUser(badUser));
    }

    @Test
    public void getUserNonExistent() throws DataAccessException {
        var actual = userDataAccess.getUser("a");
        Assertions.assertNull(actual);
    }

    @Test
    public void getUserAfterAddingMultiple() throws DataAccessException {
        var expected = new UserData("a", "p", "jkj@gmail.com");
        userDataAccess.addUser(expected);
        userDataAccess.addUser(new UserData("username", "password", "email@email.com"));
        var actual = userDataAccess.getUser("a");
        Assertions.assertEquals(expected.username(), actual.username());
        Assertions.assertEquals(expected.email(), actual.email());
    }

    @Test
    public void deleteAllAfterAddingMultiple() throws DataAccessException {
        var expected = new UserData("a", "p", "jkj@gmail.com");
        userDataAccess.addUser(expected);
        userDataAccess.addUser(new UserData("username", "password", "email@email.com"));
        var actual = userDataAccess.getUser("a");
        Assertions.assertEquals(expected.username(), actual.username());
        Assertions.assertEquals(expected.email(), actual.email());
        userDataAccess.deleteAll();
        actual = userDataAccess.getUser("a");
        Assertions.assertNull(actual);
        actual = userDataAccess.getUser("username");
        Assertions.assertNull(actual);
    }

    @Test
    public void addAuth() throws DataAccessException {
        var expected = new AuthData("authToken", "a");
        authDataAccess.addAuth(expected);
        var actual = authDataAccess.getAuthData("authToken");
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void addInvalidAuth() throws DataAccessException {
        var badAuth = new AuthData(null, "p");
        Assertions.assertThrows(DataAccessException.class,() -> authDataAccess.addAuth(badAuth));
    }

    @Test
    public void addInvalidAuthNullUsername() throws DataAccessException {
        var badAuth = new AuthData("AuthToken", null);
        Assertions.assertThrows(DataAccessException.class,() -> authDataAccess.addAuth(badAuth));
    }

    @Test
    public void getAuthNonExistent() throws DataAccessException {
        var actual = authDataAccess.getAuthData("a");
        Assertions.assertNull(actual);
    }

    @Test
    public void getAuthAfterAddingMultiple() throws DataAccessException {
        var expected = new AuthData("a", "p");
        authDataAccess.addAuth(expected);
        authDataAccess.addAuth(new AuthData("authToken","username"));
        var actual = authDataAccess.getAuthData("a");
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void deleteAllAuths() throws DataAccessException {
        var expected = new AuthData("a", "p");
        authDataAccess.addAuth(expected);
        authDataAccess.addAuth(new AuthData("authToken","username"));
        var actual = authDataAccess.getAuthData("a");
        Assertions.assertEquals(expected, actual);
        authDataAccess.deleteAll();
        actual = authDataAccess.getAuthData("a");
        Assertions.assertNull(actual);
        actual = authDataAccess.getAuthData("authToken");
        Assertions.assertNull(actual);
    }

    @Test
    public void deleteAuth() throws DataAccessException {
        var expected = new AuthData("a", "p");
        authDataAccess.addAuth(expected);
        authDataAccess.addAuth(new AuthData("authToken","username"));
        var actual = authDataAccess.getAuthData("a");
        Assertions.assertEquals(expected, actual);
        authDataAccess.deleteAuth("authToken");
        actual = authDataAccess.getAuthData("a");
        Assertions.assertEquals(expected, actual);
        actual = authDataAccess.getAuthData("authToken");
        Assertions.assertNull(actual);
    }

    @Test
    public void addGame() throws DataAccessException {
        var expected = new GameData(1, null, null, "game1", new ChessGame());
        gameDataAccess.addGame(expected);
        var actual = gameDataAccess.getGame(1);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void addInvalidGameName() throws DataAccessException {
        var badGame = new GameData(1, null, null, null, new ChessGame());
        Assertions.assertThrows(DataAccessException.class,() -> gameDataAccess.addGame(badGame));
    }

    @Test
    public void getGameNonExistent() throws DataAccessException {
        var actual = gameDataAccess.getGame(1);
        Assertions.assertNull(actual);
    }

    @Test
    public void getGameAfterAddingMultiple() throws DataAccessException {
        var expected = new GameData(1, null, null, "game1", null);
        gameDataAccess.addGame(expected);
        gameDataAccess.addGame(new GameData(2, null, null, "game2", null));
        var actual = gameDataAccess.getGame(1);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void deleteGames() throws DataAccessException {
        var expected = new GameData(1, null, null, "game1", new ChessGame());
        gameDataAccess.addGame(expected);
        gameDataAccess.addGame(new GameData(2, null, null, "game2", new ChessGame()));
        var actual = gameDataAccess.getGame(1);
        Assertions.assertEquals(expected, actual);
        gameDataAccess.deleteAll();
        Assertions.assertNull(gameDataAccess.getGame(1));
        Assertions.assertNull(gameDataAccess.getGame(2));
    }

    @Test
    public void addWhiteUsername() throws DataAccessException {
        var expected = new GameData(1, "whiteUsername", null, "game1", new ChessGame());
        gameDataAccess.addGame(new GameData(1, null, null, "game1", new ChessGame()));
        gameDataAccess.addGame(new GameData(2, null, null, "game2", new ChessGame()));
        gameDataAccess.addWhiteUsername("whiteUsername", 1);
        var actual = gameDataAccess.getGame(1);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void addWhiteUsernameMultipleGames() throws DataAccessException {
        var expected = new GameData(1, "whiteUsername", null, "game1", new ChessGame());
        gameDataAccess.addGame(new GameData(1, null, null, "game1", new ChessGame()));
        gameDataAccess.addGame(new GameData(2, null, null, "game2", new ChessGame()));
        gameDataAccess.addWhiteUsername("whiteUsername", 1);
        var actual = gameDataAccess.getGame(1);
        Assertions.assertEquals(expected, actual);
        expected = new GameData(2, "whiteUsername2", null, "game2", new ChessGame());
        gameDataAccess.addWhiteUsername("whiteUsername2", 2);
        actual = gameDataAccess.getGame(2);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void addWhiteUsernameNonexistentGame() throws DataAccessException {
        gameDataAccess.addWhiteUsername("whiteUsername", 1);
        Assertions.assertNull(gameDataAccess.getGame(1));
    }

    @Test
    public void addBlackUsername() throws DataAccessException {
        var expected = new GameData(1, null, "blackUsername", "game1", new ChessGame());
        gameDataAccess.addGame(new GameData(1, null, null, "game1", new ChessGame()));
        gameDataAccess.addGame(new GameData(2, null, null, "game2", new ChessGame()));
        gameDataAccess.addBlackUsername("blackUsername", 1);
        var actual = gameDataAccess.getGame(1);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void addBlackMultipleUsernames() throws DataAccessException {
        var expected = new GameData(1, null, "blackUsername", "game1", new ChessGame());
        gameDataAccess.addGame(new GameData(1, null, null, "game1", new ChessGame()));
        gameDataAccess.addGame(new GameData(2, null, null, "game2", new ChessGame()));
        gameDataAccess.addBlackUsername("blackUsername", 1);
        var actual = gameDataAccess.getGame(1);
        Assertions.assertEquals(expected, actual);
        expected = new GameData(2, null, "blackUsername2", "game2", new ChessGame());
        gameDataAccess.addBlackUsername("blackUsername2", 2);
        actual = gameDataAccess.getGame(2);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void addBlackUsernameNonexistentGame() throws DataAccessException {
        gameDataAccess.addBlackUsername("blackUsername", 1);
        Assertions.assertNull(gameDataAccess.getGame(1));
    }

    @Test
    public void getGames() throws DataAccessException {
        var game1 = new GameData(1, null, null, "game1", null);
        gameDataAccess.addGame(game1);
        var game2 = new GameData(2, null, null, "game2", null);
        gameDataAccess.addGame(game2);
        var actual = gameDataAccess.getGames();
        var expected = new ArrayList<GameData>();
        expected.add(game1);
        expected.add(game2);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void getGamesNoGames() throws DataAccessException {
        var actual = gameDataAccess.getGames();
        var expected = new ArrayList<GameData>();
        Assertions.assertEquals(expected, actual);
    }
}
