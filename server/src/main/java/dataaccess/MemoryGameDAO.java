package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryGameDAO implements GameDAO{
    final private Map<Integer, GameData> games = new HashMap<>();

    @Override
    public void addGame(GameData gameData) throws DataAccessException {
        if (games.containsKey(gameData.gameID())) {
            throw new DataAccessException("User already exists");
        }
        else{games.put(gameData.gameID(), gameData);
        }

    }

    @Override
    public GameData getGame(String gameID) {
        return null;
    }

    @Override
    public String addWhiteUsername(String userName, String gameID) throws DataAccessException {
        return "";
    }

    @Override
    public void addBlackUsername(String authToken, String gameID) throws DataAccessException {

    }

    @Override
    public void updateGame(ChessGame chessGame, String gameID) throws DataAccessException {

    }

    @Override
    public void deleteGame(String gameID) throws DataAccessException {
        if (games.containsKey(gameID)) {
            games.remove(gameID);
        } else {
            throw new DataAccessException("User not found");
        }

    }

    @Override
    public void deleteAll() {
        games.clear();
    }
}
