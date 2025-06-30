package pl.umcs.oop.image;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import javax.sql.DataSource;
import java.time.Instant;
import java.util.List;

@Service
public class DatabaseService {
    private final JdbcTemplate jdbcTemplate;

    public DatabaseService(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        initializeDatabase();
    }

    // Creates database table if not exists
    private void initializeDatabase() {
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS entry (
                token TEXT NOT NULL,
                x INTEGER NOT NULL,
                y INTEGER NOT NULL,
                color TEXT NOT NULL,
                timestamp TEXT NOT NULL
            )
            """);
    }

    // Inserts a pixel event into database
    public void insertPixel(String token, int x, int y, String color, Instant timestamp) {
        jdbcTemplate.update(
                "INSERT INTO entry (token, x, y, color, timestamp) VALUES(?, ?, ?, ?, ?)",
                token, x, y, color, timestamp.toString()
        );
    }

    // Deletes all entries for a token
    public int deleteByToken(String token) {
        return jdbcTemplate.update("DELETE FROM entry WHERE token=?", token);
    }

    // Retrieves all pixel events ordered by timestamp
    public List<PixelRecord> getAllPixels() {
        return jdbcTemplate.query(
                "SELECT token, x, y, color, timestamp FROM entry ORDER BY timestamp",
                (rs, rowNum) -> new PixelRecord(
                        rs.getString("token"),
                        rs.getInt("x"),
                        rs.getInt("y"),
                        rs.getString("color"),
                        Instant.parse(rs.getString("timestamp"))
                )
        );
    }

    public record PixelRecord(String token, int x, int y, String color, Instant timestamp) {}
}