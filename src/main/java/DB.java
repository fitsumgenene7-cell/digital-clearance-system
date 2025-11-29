import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DB {

    private static final String DB_URL = "jdbc:sqlite:" + System.getProperty("user.dir") + "/clearance.db";

    private DB() {}

    // Get a new connection each time
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC"); // Ensure driver is loaded
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC driver not found.", e);
        }
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {

            // Enable foreign keys for SQLite
            stmt.execute("PRAGMA foreign_keys = ON;");

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS students (
                    id TEXT PRIMARY KEY,
                    first_name TEXT NOT NULL,
                    last_name TEXT NOT NULL,
                    sex TEXT NOT NULL,
                    password TEXT NOT NULL
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS offices (
                    office_name TEXT PRIMARY KEY,
                    role TEXT NOT NULL,
                    password TEXT NOT NULL
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS penalties (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    student_id TEXT NOT NULL,
                    office_name TEXT NOT NULL,
                    status TEXT,
                    reason TEXT,
                    FOREIGN KEY(student_id) REFERENCES students(id),
                    FOREIGN KEY(office_name) REFERENCES offices(office_name)
                );
            """);

            System.out.println("Database initialized successfully!");

        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Add this method for debugging
    public static void printDatabaseInfo() {
        System.out.println("=== DATABASE INFORMATION ===");
        System.out.println("Database URL: " + DB_URL);
        System.out.println("Working directory: " + System.getProperty("user.dir"));

        java.io.File dbFile = new java.io.File("clearance.db");
        System.out.println("Database file exists: " + dbFile.exists());
        System.out.println("Database file path: " + dbFile.getAbsolutePath());
        System.out.println("Database file size: " + (dbFile.exists() ? dbFile.length() + " bytes" : "N/A"));
        System.out.println("============================");
    }
}