package dataaccess;

import chess.ChessGame;
import model.GameData;

public interface GameDAO {

    //gameID	int
    //whiteUsername	String
    //blackUsername	String
    //gameName	String
    //game	ChessGame
    void addGame(GameData gameData) throws DataAccessException;

    GameData getGame(String gameID);

    String addWhiteUsername(String userName, String gameID) throws DataAccessException;

    void addBlackUsername(String authToken, String gameID) throws DataAccessException;

    void updateGame(ChessGame chessGame, String gameID) throws DataAccessException;

    void deleteGame(String gameID) throws DataAccessException;

    void deleteAll();
}
