import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DB {

    private static final String DB_URL = "jdbc:sqlite:clearance.db";
    private static Connection connection;

    private DB() {}

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (connection == null) {
            connection = DriverManager.getConnection(DB_URL);
        }
        return connection;
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {

            // Students table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS students (
                    id TEXT PRIMARY KEY,
                    full_name TEXT NOT NULL,
                    password TEXT NOT NULL
                );
            """);

            // Offices table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS offices (
                    office_name TEXT PRIMARY KEY,
                    role TEXT NOT NULL,
                    password TEXT NOT NULL
                );
            """);


            // Penalties table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS penalties (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    student_id TEXT NOT NULL,
                    office_id INTEGER NOT NULL,
                    status TEXT,
                    reason TEXT,
                    FOREIGN KEY(student_id) REFERENCES students(id),
                    FOREIGN KEY(office_id) REFERENCES offices(id)
                );
            """);

            System.out.println("Database initialized successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void closeConnection() {
        try {
            if (connection != null) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}