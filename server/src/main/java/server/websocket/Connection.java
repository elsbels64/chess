package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class Connection {
    public String username;
    public Session session;



    public Connection(String userName, Session session) {
        this.username = userName;
        this.session = session;
    }

    public String getUsername() {
        return username;
    }

    public void send(ServerMessage msg) throws IOException {
        Gson g = new Gson();
        this.session.getRemote().sendString(g.toJson(msg));
    }
}