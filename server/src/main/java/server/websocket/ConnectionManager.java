package server.websocket;

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

    public void add(int gameID, String visitorName, Session session) {
        var connection = new Connection(visitorName, session);
        connections.computeIfAbsent(gameID, k -> new CopyOnWriteArrayList<>()).add(connection);
    }

    public void removePlayer(int gameID, String username) {
        List<Connection> connectionList = connections.get(gameID); // Get the list for the gameID
        if (connectionList != null) { // Check if the list exists
            connectionList.removeIf(connection -> connection.username.equals(username)); // Remove by username
        }
    }

    public void send_not_user(int gameID, String excludeUsername, ServerMessage notification) throws IOException {
        //broadcast is send to all
        var removeList = new ArrayList<Connection>();
        List<Connection> connectionList = connections.get(gameID);
        for (var connection : connectionList) {
            if (connection.session.isOpen()) {
                if (!connection.username.equals(excludeUsername)) {
                    connection.send(notification);
                }
            } else {
                removeList.add(connection);
            }
        }

        for (var connection : removeList) {
            removePlayer(gameID, connection.username);
        }
    }

    public void send_everyone(int gameID, String excludeUsername, ServerMessage notification) throws IOException {
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
            removePlayer(gameID, connection.username);
        }
    }

    public void send_user(int gameID, String excludeUsername, ServerMessage notification) throws IOException {
        //broadcast is send to all
        var removeList = new ArrayList<Connection>();
        List<Connection> connectionList = connections.get(gameID);
        for (var connection : connectionList) {
            if (connection.session.isOpen()) {
                if (connection.username.equals(excludeUsername)) {
                    connection.send(notification);
                }
            } else {
                removeList.add(connection);
            }
        }

        for (var connection : removeList) {
            removePlayer(gameID, connection.username);
        }
    }
}
