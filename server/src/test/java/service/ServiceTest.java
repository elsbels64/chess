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
    public void registerUsertwice() throws BadRequestException, AlreadyTakenException {
        var userData = new UserData("userName", "Password", "email@email.com");
        service.registerUser(userData);

        Assertions.assertThrows(DataAccessException.class, () -> service.registerUser(userData));
    }
    @Test
    public void registerUserSuccess() throws BadRequestException, AlreadyTakenException {
        var userData = new UserData("newUser", "Password", "newUser@email.com");

        // Register user and check that it doesn't throw an exception
        var authData = service.registerUser(userData);
        Assertions.assertNotNull(authData);  // Verify that authData is returned
        Assertions.assertEquals("newUser", authData.username());  // Verify correct username
    }
}
