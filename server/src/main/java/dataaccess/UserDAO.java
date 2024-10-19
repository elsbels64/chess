package dataaccess;

import model.UserData;

public interface UserDAO {
    void addUser(UserData userData);

    UserData getUser(String username);

    void deleteUser(String username) throws DataAccessException;

    void deleteAll();
}
