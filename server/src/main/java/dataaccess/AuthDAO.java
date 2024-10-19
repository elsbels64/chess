package dataaccess;

import model.AuthData;
import model.UserData;

public interface AuthDAO {
    void addAuth(AuthData authData);

    AuthData getAuthData(String authToken);

    String getUsername(String authToken);

    void deleteAuth(String authToken) throws DataAccessException;

    void deleteAll();
}
