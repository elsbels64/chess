package service;

import dataaccess.DataAccessException;
import model.UserData;
import dataaccess.MemoryDataAccess;

public class Service {
    MemoryDataAccess dataAccess = new MemoryDataAccess();
    public UserData registerUser(UserData newUser) throws DataAccessException {
        if(dataAccess.getUser(newUser.username())==null) {
            return dataAccess.addUser(newUser);
        }else{
            throw new DataAccessException("User already exists");
        }
    }

    public UserData getUser(UserData newUser) throws DataAccessException {
        return dataAccess.addUser(newUser);
    }
}
