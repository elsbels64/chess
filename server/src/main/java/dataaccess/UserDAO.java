package dataaccess;

import model.UserData;

public interface UserDAO {
    public boolean checkPassword(String password, String databasePassword);

    void addUser(UserData userData) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    void deleteAll() throws DataAccessException;
}
