package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryGameDAO implements GameDAO{
    final private Map<Integer, GameData> games = new HashMap<>();

    @Override
    public void addGame(GameData gameData){
        games.put(gameData.gameID(), gameData);
    }

    @Override
    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    @Override
    public void addWhiteUsername(String userName, int gameID){
        GameData gameData = games.get(gameID);
        games.remove(gameID);
        GameData updatedGameData = new GameData(gameData.gameID(), userName,gameData.blackUsername(),gameData.gameName(),gameData.game());
        games.put(updatedGameData.gameID(), updatedGameData);
    }

    @Override
    public void addBlackUsername(String userName, int gameID) {
        GameData gameData = games.get(gameID);
        games.remove(gameID);
        GameData updatedGameData = new GameData(gameData.gameID(), gameData.whiteUsername(), userName, gameData.gameName(),gameData.game());
        games.put(updatedGameData.gameID(), updatedGameData);
    }

    @Override
    public void updateGame(ChessGame chessGame, int gameID) {
        GameData gameData = games.get(gameID);
        games.remove(gameID);
        GameData updatedGameData = new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(),chessGame);
        games.put(updatedGameData.gameID(), updatedGameData);
    }

    @Override
    public void deleteGame(int gameID) throws DataAccessException {
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
