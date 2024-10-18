package dataaccess;

import model.UserData;

public interface UserDAO {
    void addUser(UserData userData) throws DataAccessException;

//    Collection<Pet> listPets() throws ResponseException;

    UserData getUser(String username) throws DataAccessException;

    void deleteUser(String username) throws DataAccessException;

    void deleteAllUsers();
}
