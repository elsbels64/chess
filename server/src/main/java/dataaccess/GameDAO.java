package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.sql.SQLException;
import java.util.List;

public interface GameDAO {

    //gameID	int
    //whiteUsername	String
    //blackUsername	String
    //gameName	String
    //game	ChessGame
    void addGame(GameData gameData) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    List<GameData> getGames() throws DataAccessException;

    void addWhiteUsername(String userName, int gameID) throws DataAccessException;

    void addBlackUsername(String userName, int gameID) throws DataAccessException;

    void removeWhiteUsername(int gameID) throws DataAccessException;

    void removeBlackUsername(int gameID) throws DataAccessException;

    void updateGame(ChessGame chessGame, int gameID) throws DataAccessException;

    void deleteAll() throws DataAccessException;
}
