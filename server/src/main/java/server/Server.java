package server;

import dataaccess.DataAccessException;
import model.UserData;
import spark.*;
import com.google.gson.Gson;
import service.Service;

import java.security.NoSuchAlgorithmException;


public class Server {
    private final Service service;

    public Server() {
        this.service = new Service();
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::createUser);
        Spark.delete("/db", this::clearAllData);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private String createUser(Request req, Response res) throws DataAccessException, NoSuchAlgorithmException {
        var g = new Gson();
        var newUser = g.fromJson(
            String.valueOf(req.body()), UserData.class);
        var resUserData = service.registerUser(newUser);
        return g.toJson(resUserData);
    }

    private String clearAllData(Request req, Response res){
        service.clearDatabases();
        return "{}";
    }

}
