package dataaccess;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import model.UserData;

public class DataAccessTest {
    @Test
    public void getUserNonExistent(){
        var dataAccess = new MemoryUserDAO();
        var actual = dataAccess.getUser("a");
        Assertions.assertNull(actual);
    }

    @Test
    public void registerUser() throws DataAccessException {
        var dataAccess = new MemoryUserDAO();
        var expected = new UserData("a", "p", "jkj@gmail.com");
        dataAccess.addUser(expected);
        var actual = dataAccess.getUser("a");
        Assertions.assertEquals(expected, actual);
    }


}
