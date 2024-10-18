package dataaccess;

import model.UserData;

import java.util.Collection;

public interface DataAccess {
    UserData addUser(UserData userData) throws DataAccessException;

//    Collection<Pet> listPets() throws ResponseException;

    UserData getUser(String username) throws DataAccessException;

    void deleteUser(String username) throws DataAccessException;

    void deleteAllUsers() throws DataAccessException;
}
