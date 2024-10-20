package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    public List<GameData> getGames(){
        List<GameData> gameDataList = new ArrayList<>();
        for (GameData gameData : games.values()) {
            gameDataList.add(gameData);
        }
        return gameDataList;
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
        GameData updatedGameData = new GameData(gameID, gameData.whiteUsername(), userName, gameData.gameName(),gameData.game());
        games.put(gameID, updatedGameData);
    }

    @Override
    public void deleteAll() {
        games.clear();
    }
}
