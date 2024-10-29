package dataaccess;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.UserData;
import service.Service;

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
        Assertions.assertEquals(expected, actual);
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
    public void getUser() throws DataAccessException {
        var expected = new UserData("a", "p", "jkj@gmail.com");
        userDataAccess.addUser(expected);
        userDataAccess.addUser(new UserData("username", "password", "email@email.com"));
        var actual = userDataAccess.getUser("a");
        Assertions.assertEquals(expected.username(), actual.username());
        Assertions.assertEquals(expected.email(), actual.email());
    }


}
