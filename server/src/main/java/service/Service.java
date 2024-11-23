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
    private UserDAO userDataAccess;
    private AuthDAO authDataAccess;
    private GameDAO gameDataAccess;

    public Service(UserDAO userDataAccess, AuthDAO authDataAccess, GameDAO gameDataAccess) {
        this.userDataAccess = userDataAccess;
        this.authDataAccess = authDataAccess;
        this.gameDataAccess = gameDataAccess;
    }


    public AuthData registerUser(UserData newUser) throws AlreadyTakenException, BadRequestException, DataAccessException {
        if((newUser.password() == null || newUser.email()==null)|| newUser.username() == null ){
            throw new BadRequestException("A field is null");
        }
        if(userDataAccess.getUser(newUser.username())!=null) {
            throw new AlreadyTakenException("User already exists");
        }else{
            userDataAccess.addUser(newUser);
            String authToken = generateAuthToken();
            AuthData auth = new AuthData(authToken, newUser.username());
            authDataAccess.addAuth(auth);
            return auth;
        }
    }

    public AuthData loginUser(UserData user) throws BadRequestException, UnauthorizedException, DataAccessException {
        if(user.password() == null || user.username() == null ){
            throw new BadRequestException("A field is null");
        }
        if(userDataAccess.getUser(user.username())==null) {
            throw new UnauthorizedException("Does not exist");
        }else{
            UserData userInDataBase = userDataAccess.getUser(user.username());
            if (userDataAccess.checkPassword(user.password(), userInDataBase.password())) {
                String authToken = generateAuthToken();
                AuthData auth = new AuthData(authToken, user.username());
                authDataAccess.addAuth(auth);
                return auth;
            } else {
                throw new UnauthorizedException("Wrong password");
            }
        }
    }

    public void logoutUser(String authToken) throws UnauthorizedException, DataAccessException {
        checkAuthToken(authToken);
        authDataAccess.deleteAuth(authToken);
    }

    public List<GameData> getGames(String authToken) throws UnauthorizedException, DataAccessException {
        checkAuthToken(authToken);
        return gameDataAccess.getGames();
    }

    public int createGame(String authToken, String gameName) throws UnauthorizedException, DataAccessException {
        checkAuthToken(authToken);
        //I may need to add something to check if a game of the same name already exists
        Random random = new Random();
        int gameID = random.nextInt(1_000_000) + 1;
        GameData newGameData = new GameData(gameID,null, null,gameName, new ChessGame());
        gameDataAccess.addGame(newGameData);
        return gameID;
    }

    public void joinGame(String authToken,  int gameID, String playerColor)
            throws UnauthorizedException, AlreadyTakenException, BadRequestException, DataAccessException {
        if(playerColor==null){
            throw new BadRequestException("no player color provided.");
        }
        if(gameDataAccess.getGame(gameID)==null){
            throw new BadRequestException("wrong gameID");
        }
        String username = checkAuthToken(authToken);
        GameData gameData = gameDataAccess.getGame(gameID);
        if(playerColor.equals("WHITE")){
            if(gameData.whiteUsername() != null){
                throw new AlreadyTakenException("there is already a white player");
            }
            gameDataAccess.addWhiteUsername(username, gameID);
        }
        else if(playerColor.equals("BLACK")){
            if(gameData.blackUsername() != null){
                throw new AlreadyTakenException("there is already a black player");
            }
            gameDataAccess.addBlackUsername(username, gameID);
        }else{
            throw new BadRequestException("Not a player color.");
        }
    }

    //update game data . there is an update sql command you can use
    public void updateGame(String authToken, int gameID, ChessGame chessGame) throws BadRequestException, UnauthorizedException, DataAccessException {
        if(chessGame==null){
            throw new BadRequestException("no chessGame");
        }
        if(authToken==null){
            throw new BadRequestException("no authToken");
        }
        if(gameDataAccess.getGame(gameID)==null){
            throw new BadRequestException("wrong gameID");
        }
        checkAuthToken(authToken);
        gameDataAccess.updateGame(chessGame, gameID);
    }

    public static String generateAuthToken(){
        return UUID.randomUUID().toString();
    }

    public String checkAuthToken(String authToken) throws UnauthorizedException, DataAccessException {
        AuthData authData = authDataAccess.getAuthData(authToken);
        if(authData==null){
            throw new UnauthorizedException("Auth does not exist in database");
        }
        return authData.username();
    }

    public void clearDatabases() throws DataAccessException {
        userDataAccess.deleteAll();
        authDataAccess.deleteAll();
        gameDataAccess.deleteAll();
    }
    //for game ID just generate a random number and use that as the gameID
}
