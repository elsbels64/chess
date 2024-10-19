package server;
import dataaccess.*;
import model.UserData;
import model.Error;
import model.GameData;
import model.AuthData;
import spark.*;
import com.google.gson.Gson;
import service.Service;


public class Server {
    MemoryUserDAO userDataAccess = new MemoryUserDAO();
    MemoryAuthDAO authDataAccess = new MemoryAuthDAO();
    MemoryGameDAO gameDataAccess = new MemoryGameDAO();
    private final Service service;


    public Server() {
        this.service = new Service(userDataAccess, authDataAccess, gameDataAccess);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::createUser);
        Spark.post("/session", this::loginUser);
        Spark.delete("/db", this::clearAllData);
        Spark.delete("/session", this::logoutUser);
        Spark.post("/game",this::createGame);
        Spark.put("/game",this::joinGame);

        Spark.exception(DataAccessException.class, this::dataAccessExceptionHandler);
        Spark.exception(AlreadyTakenException.class, this::alreadyTakenExceptionHandler);
        Spark.exception(BadRequestException.class, this::badRequestExceptionHandler);
        Spark.exception(UnauthorizedException.class, this::unauthorizedExceptionHandler);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private String createUser(Request req, Response res) throws AlreadyTakenException, BadRequestException {
        var g = new Gson();
        var newUser = g.fromJson(
            String.valueOf(req.body()), UserData.class);
        var resUserData = service.registerUser(newUser);
        return g.toJson(resUserData);
    }

    private String loginUser(Request req, Response res) throws BadRequestException, UnauthorizedException {
        var g = new Gson();
        var user = g.fromJson(
                String.valueOf(req.body()), UserData.class);
        var resUserData = service.loginUser(user);
        return g.toJson(resUserData);
    }

    private String clearAllData(Request req, Response res){
        service.clearDatabases();
        return "{}";
    }

    private String logoutUser(Request req, Response res) throws UnauthorizedException, DataAccessException {
        String authToken = req.headers("Authorization");
        service.logoutUser(authToken);
        return "{}";
    }

    private String createGame(Request req, Response res) throws UnauthorizedException, DataAccessException {
        String authToken = req.headers("Authorization");
        var g = new Gson();
        GameData gameData = g.fromJson(req.body(), GameData.class);
        String gameName = gameData.gameName();
        int gameID = service.createGame(authToken, gameName);
        return "{\"gameID\": " + gameID + "}";
    }

    private String joinGame(Request req, Response res) {
        String authToken = req.headers("Authorization");
        
        service.joinGame(authToken,);
        return "{}";
    }

    private void dataAccessExceptionHandler(Exception exception, Request req, Response res) {
//        res.status(exception.StatusCode());
        var g = new Gson();
        res.status(500);  // Set status code to 400 Bad Request
        Error error= new Error("Error: " + exception.getMessage());
        res.body(g.toJson(error));
    }

    private void alreadyTakenExceptionHandler(Exception exception, Request req, Response res) {
        var g = new Gson();
        res.status(403);  // Set status code to 400 Bad Request
        Error error= new Error("Error: " + exception.getMessage());
        res.body(g.toJson(error));
    }

    private void badRequestExceptionHandler(Exception exception, Request req, Response res) {
        var g = new Gson();
        res.status(400);  // Set status code to 400 Bad Request
        Error error= new Error("Error: " + exception.getMessage());
        res.body(g.toJson(error));
    }

    private void unauthorizedExceptionHandler(Exception exception, Request req, Response res) {
        var g = new Gson();
        res.status(401);  // Set status code to 400 Bad Request
        Error error= new Error("Error: " + exception.getMessage());
        res.body(g.toJson(error));
    }
}
