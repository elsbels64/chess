package serverFacade;

import model.GameData;

import java.util.List;

public record ServerFacadeListGamesReturn(String response, List<GameData> gamesList) {
}
