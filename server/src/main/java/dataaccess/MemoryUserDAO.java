package dataaccess;

import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements UserDAO {
    final private Map<String, UserData> users = new HashMap<>();

    public Map<String, UserData> getUsers() {
        return users;
    }

    @Override
    public void addUser(UserData userData) throws DataAccessException {
        if (users.containsKey(userData.username())) {
            throw new DataAccessException("User already exists");
        }
        else{users.put(userData.username(), userData);
        }
    }

    @Override
    public UserData getUser(String username){
        return users.get(username);
    }

    @Override
    public void deleteUser(String username) throws DataAccessException {
        if (users.containsKey(username)) {
            users.remove(username);
        } else {
            throw new DataAccessException("User not found");
        }
    }

    @Override
    public void deleteAll(){
        users.clear();
    }
}
