package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryAuthDAO implements AuthDAO{
    final private Map<String, AuthData> auths = new HashMap<>();

    @Override
    public void addAuth(AuthData authData){
        auths.put(authData.authToken(), authData);
    }

    @Override
    public AuthData getAuthData(String authToken) {
        return auths.get(authToken);
    }

    @Override
    public String getUsername(String authToken) {
        return auths.get(authToken).username();
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        if (auths.containsKey(authToken)) {
            auths.remove(authToken);
        } else {
            throw new DataAccessException("authorization Token not found");
        }
    }

    @Override
    public void deleteAll() {
        auths.clear();
    }
}
