package dataaccess;

import com.google.gson.Gson;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySQLUserDAO implements UserDAO{


    public MySQLUserDAO() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public boolean checkPassword(String password, String databasePassword) {
        return BCrypt.checkpw(password, databasePassword);
    }

    @Override
    public void addUser(UserData userData) throws DataAccessException {
        var statement = "INSERT INTO users (username, email, password, json) VALUES (?, ?, ?, ?)";
        var json = new Gson().toJson(userData);
        String password = createPasswordHash(userData.password());
        var id = executeUpdate(statement, userData.username(), userData.email(), password, json);
    }

    String createPasswordHash(String clearTextPassword) {
        return BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email, json FROM users WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    private UserData readUser(ResultSet rs) throws SQLException {
        String username = rs.getString("username");
        var password = rs.getString("password");
        var email = rs.getString("email");
        var json = rs.getString("json");
        var userData = new Gson().fromJson(json, UserData.class);
        return userData;
    }

    @Override
    public void deleteAll() throws DataAccessException {
        var statement = "TRUNCATE users";
        executeUpdate(statement);
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p){ ps.setString(i + 1, p);}
                    else if (param == null) {ps.setNull(i + 1, NULL);}
                }
                ps.executeUpdate();
                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }



    private final String createStatements =
            """
            CREATE TABLE IF NOT EXISTS  users (
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256),
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`username`)
            )
            """
    ;


    private void configureDatabase() throws DataAccessException, DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(createStatements)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
