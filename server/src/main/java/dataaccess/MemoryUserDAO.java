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
    public void addUser(UserData userData) {
        users.put(userData.username(), userData);
    }

    @Override
    public UserData getUser(String username){
        return users.get(username);
    }

    @Override
    public void deleteAll(){
        users.clear();
    }
}
