package serverFacade;

import com.google.gson.Gson;
import model.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Objects;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public String registerUser(String username, String password, String email) {
        UserData userData = new UserData(username, password, email);

        try{
            AuthData authData = makeRequest("POST","/user", userData, AuthData.class, "" );
            return authData.authToken();
        }catch(Exception ex) {
            if(Objects.equals(ex.getMessage(), "403")){
                return "failure: that user already exists";
            }
            if(Objects.equals(ex.getMessage(), "400")){
                return "failure: one of your inputs was empty that shouldn't have been";
            }
            return "failure: something went wrong on our end";
        }
    }

    public String loginUser(String username, String password) {
        UserData userData = new UserData(username, password, null);

        try{
            AuthData authData = makeRequest("POST","/session", userData, AuthData.class, "" );
            return authData.authToken();
        }catch(Exception ex) {
            if(Objects.equals(ex.getMessage(), "401")){
                return "failure: username or password was wrong";
            }
            return "failure: something went wrong on our end";
        }
    }

    public String logoutUser(String authToken) {
        try{
            Object result = makeRequest("DELETE","/session", null, Object.class, authToken);
            return "You have been successfully logged out";
        }catch(Exception ex) {
            if(Objects.equals(ex.getMessage(), "401")){
                return "failure: you are not actually logged in";
            }
            return "failure: something went wrong on our end";
        }
    }

    public String createGame(String gameName, String authToken) {
        GameName gameNameObj = new GameName(gameName);
        try{
            GameID gameID = makeRequest("POST","/game", gameNameObj, GameID.class, authToken );
            return "" + gameID.gameID();
        }catch(Exception ex) {
            if(Objects.equals(ex.getMessage(), "401")){
                return "failure: you don't have the required authorization";
            }
            if(Objects.equals(ex.getMessage(), "400")){
                return "failure: one of your inputs was empty that shouldn't have been";
            }
            return "failure: something went wrong on our end";
        }
    }

    private <T> T makeRequest(String method, String path, Object request,  Class<T> responseClass, String header) throws Exception {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if(!header.equals("")){
            http.setRequestProperty("Authorization", header);
            http.setRequestProperty("Content-Type", "application/json"); // Assuming JSON body format
            http.setRequestProperty("Accept", "application/json");
            }

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
    }


    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, Exception {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new Exception(""+status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
