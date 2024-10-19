package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;


import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Service {
    MemoryUserDAO userDataAccess;
    MemoryAuthDAO authDataAccess;
    MemoryGameDAO gameDataAccess;

    public Service(MemoryUserDAO userDataAccess, MemoryAuthDAO authDataAccess, MemoryGameDAO gameDataAccess) {
        this.userDataAccess = userDataAccess;
        this.authDataAccess = authDataAccess;
        this.gameDataAccess = gameDataAccess;
    }

    public AuthData registerUser(UserData newUser) throws AlreadyTakenException, BadRequestException {
        if((newUser.password() == null || newUser.email()==null)|| newUser.username() == null ){
            throw new BadRequestException("A field is null");
        }
        if(userDataAccess.getUser(newUser.username())!=null) {
            throw new AlreadyTakenException("User already exists");
        }else{
            userDataAccess.addUser(newUser);
            String authToken = generateAuthToken(newUser);
            AuthData auth = new AuthData(authToken, newUser.username());
            authDataAccess.addAuth(auth);
            return auth;
        }
    }

    public AuthData loginUser(UserData user) throws BadRequestException, UnauthorizedException {
        if(user.password() == null || user.username() == null ){
            throw new BadRequestException("A field is null");
        }
        if(userDataAccess.getUser(user.username())==null) {
            throw new UnauthorizedException("Does not exist");
        }else{
            UserData userInDataBase = userDataAccess.getUser(user.username());
            if(!user.password().equals(userInDataBase.password())){
                throw new UnauthorizedException("Wrong password");
            }
            String authToken = generateAuthToken(user);
            AuthData auth = new AuthData(authToken, user.username());
            authDataAccess.addAuth(auth);
            return auth;
        }
    }

    public void logoutUser(String authToken) throws UnauthorizedException, DataAccessException {
        checkAuthToken(authToken);
        authDataAccess.deleteAuth(authToken);
    }

    public List<GameData> getGames(String authToken) throws UnauthorizedException {
        checkAuthToken(authToken);
        return gameDataAccess.getGames();
    }

    public int createGame(String authToken, String gameName) throws UnauthorizedException, DataAccessException {
        checkAuthToken(authToken);
        //I may need to add something to check if a game of the same name already exists
        int gameID = generateGameID();
        GameData newGameData = new GameData(gameID,"", "",gameName, new ChessGame());
        gameDataAccess.addGame(newGameData);
        return gameID;
    }

    public void joinGame(String authToken,  int gameID, String playerColor) throws UnauthorizedException, AlreadyTakenException, DataAccessException, BadRequestException {
        if(playerColor==null){
            throw new BadRequestException("no player color provided.");
        }
        if(gameDataAccess.getGame(gameID)==null){
            throw new BadRequestException("wrong gameID");
        }
        String username = checkAuthToken(authToken);
        GameData gameData = gameDataAccess.getGame(gameID);
        if(playerColor.equals("white")){
            if(gameData.whiteUsername()!=null){
                throw new AlreadyTakenException("there is already a white player");
            }
            gameDataAccess.addWhiteUsername(username, gameID);
        }
        if(playerColor.equals("black")){
            if(gameData.blackUsername()!=null){
                throw new AlreadyTakenException("there is already a black player");
            }
            gameDataAccess.addBlackUsername(username, gameID);
        }
    }

    public static String generateAuthToken(UserData user){
        return UUID.randomUUID().toString();
    }

    public int generateGameID(){
        Random random = new Random();
        return random.nextInt(1_000_000) + 1;
    }

    public String checkAuthToken(String authToken)throws UnauthorizedException{
        AuthData authData = authDataAccess.getAuthData(authToken);
        if(authData==null){
            throw new UnauthorizedException("Auth does not exist in database");
        }
        return authData.username();
    }

    public void clearDatabases(){
        userDataAccess.deleteAll();
        authDataAccess.deleteAll();
        gameDataAccess.deleteAll();
    }

    //for game ID just generate a random number and use that as the gameID
}
