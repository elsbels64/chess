package dataaccess;

import chess.ChessGame;
import model.GameData;

public interface GameDAO {

    //gameID	int
    //whiteUsername	String
    //blackUsername	String
    //gameName	String
    //game	ChessGame
    void addGame(GameData gameData);

    GameData getGame(int gameID);

    void addWhiteUsername(String userName, int gameID);

    void addBlackUsername(String userName, int gameID);

    void updateGame(ChessGame chessGame, int gameID);

    void deleteGame(int gameID) throws DataAccessException;

    void deleteAll();
}
