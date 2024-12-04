package server.websocket;

import dataaccess.DataAccessException;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, List<Connection>> connections = new ConcurrentHashMap<>();

    public void add(int gameID, Connection connection) {
//        var connection = new Connection(authToken, session);
        connections.computeIfAbsent(gameID, k -> new CopyOnWriteArrayList<>()).add(connection);
    }

    public void removePlayer(int gameID, String authToken) throws DataAccessException {
        List<Connection> connectionList = connections.get(gameID); // Get the list for the gameID
        if (connectionList != null) { // Check if the list exists
            connectionList.removeIf(connection -> connection.authToken.equals(authToken)); // Remove by username
        }else{
            throw new DataAccessException("There is nobody officially in this game");
        }
    }

    public void send_not_user(int gameID, String excludeAuthToken, ServerMessage notification) throws IOException, DataAccessException {
        //broadcast is send to all
        var removeList = new ArrayList<Connection>();
        List<Connection> connectionList = connections.get(gameID);
        for (var connection : connectionList) {
            if (connection.session.isOpen()) {
                if (!connection.authToken.equals(excludeAuthToken)) {
                    connection.send(notification);
                }
            } else {
                removeList.add(connection);
            }
        }

        for (var connection : removeList) {
            removePlayer(gameID, connection.authToken);
        }
    }

    public void send_everyone(int gameID, String authToken, ServerMessage notification) throws IOException, DataAccessException {
        //broadcast is send to all
        var removeList = new ArrayList<Connection>();
        List<Connection> connectionList = connections.get(gameID);
        for (var connection : connectionList) {
            if (connection.session.isOpen()) {
                connection.send(notification);
            } else {
                removeList.add(connection);
            }
        }

        for (var connection : removeList) {
            removePlayer(gameID, connection.authToken);
        }
    }

    public void send_user(int gameID, String authToken, ServerMessage notification) throws IOException, DataAccessException {
        //broadcast is send to all
        var removeList = new ArrayList<Connection>();
        List<Connection> connectionList = connections.get(gameID);
        for (var connection : connectionList) {
            if (connection.session.isOpen()) {
                if (connection.authToken.equals(authToken)) {
                    connection.send(notification);
                }
            } else {
                removeList.add(connection);
            }
        }

        for (var connection : removeList) {
            removePlayer(gameID, connection.authToken);
        }
    }
}
