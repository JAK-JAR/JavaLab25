package server;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class Database {
    private Connection conn;

    public Database(String dbFile) throws SQLException {
        conn = DriverManager.getConnection("jdbc:sqlite:" + dbFile);
    }

    public boolean authenticate(String login, String password) {
        try(PreparedStatement st = conn.prepareStatement("SELECT * FROM users WHERE login = ? AND password = ?")) {
            st.setString(1, login);
            st.setString(2, password);
            ResultSet rs = st.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);

        }
    }

    public void updateLeaderboard(String winner, String loser) {
        try (PreparedStatement stWin = conn.prepareStatement(
                "UPDATE users SET points = points + 1 WHERE login = ?")) {
            stWin.setString(1, winner);
            stWin.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try (PreparedStatement stLose = conn.prepareStatement(
                "UPDATE users SET points = points - 1 WHERE login = ?")) {
            stLose.setString(1, loser);
            stLose.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public Map<String, Integer> getLeaderboard() {
        // "SELECT login, points FROM users ORDER BY points DESC";
        Map<String, Integer> leaderboard = new HashMap<>();
        try (Statement st = conn.createStatement()) {
            ResultSet rs = st.executeQuery("SELECT login, points FROM users ORDER BY points DESC");
            while (rs.next()) {
                leaderboard.put(rs.getString("login"), rs.getInt("points"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return leaderboard;
    }
}
