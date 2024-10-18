package dataaccess;

import model.AuthData;
import model.UserData;

public interface AuthDAO {
    void addAuth(AuthData authData) throws DataAccessException;

    AuthData getAuthData(String authToken) throws DataAccessException;

    String getUsername(String authToken) throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException;

    void deleteAll();
}
