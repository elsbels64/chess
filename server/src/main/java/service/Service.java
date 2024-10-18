package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import model.AuthData;
import model.UserData;
import dataaccess.MemoryUserDAO;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import static javax.crypto.Cipher.SECRET_KEY;

public class Service {
    MemoryUserDAO userDataAccess = new MemoryUserDAO();
    MemoryAuthDAO authDataAccess = new MemoryAuthDAO();
    MemoryGameDAO gameDataAccess = new MemoryGameDAO();
    public AuthData registerUser(UserData newUser) throws DataAccessException, NoSuchAlgorithmException {
        if(userDataAccess.getUser(newUser.username())!=null) {
            throw new DataAccessException("User already exists");
        }else{
            userDataAccess.addUser(newUser);
            String authtoken = generateAuthToken(newUser);
            AuthData auth = new AuthData(authtoken, newUser.username());
            authDataAccess.addAuth(auth);
            return auth;
        }
    }

    public UserData loginUser(UserData newUser) throws DataAccessException {
        if(userDataAccess.getUser(newUser.username())==null) {
            throw new DataAccessException("User already exists");
        }else{
        return userDataAccess.getUser(newUser.username());
        }
    }

    public static String generateAuthToken(UserData user) throws NoSuchAlgorithmException {
        // Combine user data with a secret key
        String combinedData = user.username() + ":" + user.Password() + ":" + user.email() + ":" + SECRET_KEY;

        // Get instance of SHA-256
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        // Hash the combined data
        byte[] hashedBytes = digest.digest(combinedData.getBytes(StandardCharsets.UTF_8));

        // Encode the hashed bytes to Base64
        return Base64.getEncoder().encodeToString(hashedBytes);
    }

    public void clearDatabases(){
        userDataAccess.deleteAll();
        authDataAccess.deleteAll();
        gameDataAccess.deleteAll();
    }
}
