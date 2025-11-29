import java.sql.*;

public class SeedData {


    public static void seedStudents() {
        String sql = """
        INSERT OR IGNORE INTO students (id, first_name, last_name, sex, password) VALUES
        ('1459/16','BEREKET','FIKRE ABIYO','Male','1234'),
        ('3113/16','NAOL','GIRMA TEFERA','Male','1234'),
        ('1420/16','BEHAILU','HAGOS ADANE','Male','1234'),
        ('3170/16','NATNAEL','REBUMA BEKELE','Male','1234'),
        ('2398/16','HENOK','LEMA KIFLE','Male','1234'),
        ('1598/16','BINIAM','MESFIN WELDETSADIK','Male','1234'),
        ('1996/16','ESMAEL','YASIN MUHAMMED','Male','1234');
        -- continue with all remaining students, each separated by a comma
    """;

        try (Connection conn = DB.getConnection(); Statement stmt = conn.createStatement()) {
            int rowsAffected = stmt.executeUpdate(sql);
            System.out.println("Students seeded successfully! Rows affected: " + rowsAffected);
        } catch (SQLException e) {
            System.err.println("Error seeding students: " + e.getMessage());
            e.printStackTrace();
        }
    }

        public static void seedOffices() {
        String sql = """
            INSERT OR IGNORE INTO offices (office_name, role, password) VALUES
            ('Dormitory','Dorm Manager','1'),
            ('Sport','Sport Officer','2'),
            ('Cafeteria','Cafeteria Manager','3'),
            ('Library','Librarian','4'),
            ('Police Office','Police Officer','5'),
            ('Faculty','Faculty Admin','6'),
            ('Student Service','Student Service Officer','7');
        """;

        try (Connection conn = DB.getConnection(); Statement stmt = conn.createStatement()) {
            int rowsAffected = stmt.executeUpdate(sql);
            System.out.println("Offices seeded successfully! Rows affected: " + rowsAffected);
        } catch (SQLException e) {
            System.err.println("Error seeding offices: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ADD THESE RETRIEVAL METHODS:
    public static void displayAllStudents() {
        String sql = "SELECT * FROM students";

        try (Connection conn = DB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n=== STUDENTS IN DATABASE ===");

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println("ID: " + rs.getString("id") +
                        ", Name: " + rs.getString("first_name") + " " + rs.getString("last_name") +
                        ", Sex: " + rs.getString("sex"));
            }

            if (!found) {
                System.out.println("No students found in the database!");
            }

        } catch (SQLException e) {
            System.err.println("Error displaying students: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void displayAllOffices() {
        String sql = "SELECT * FROM offices";

        try (Connection conn = DB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n=== OFFICES IN DATABASE ===");

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println("Office: " + rs.getString("office_name") +
                        ", Role: " + rs.getString("role") +
                        ", Password: " + rs.getString("password"));
            }

            if (!found) {
                System.out.println("No offices found in the database!");
            }

        } catch (SQLException e) {
            System.err.println("Error displaying offices: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Add this method to check if tables exist and have data
    public static void debugDatabase() {
        System.out.println("\n=== DATABASE DEBUG INFO ===");

        // Check students table
        try (Connection conn = DB.getConnection();
             Statement stmt = conn.createStatement()) {

            // Check if students table exists and has data
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM students");
            if (rs.next()) {
                System.out.println("Students table count: " + rs.getInt("count"));
            }

            // Check if offices table exists and has data
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM offices");
            if (rs.next()) {
                System.out.println("Offices table count: " + rs.getInt("count"));
            }

        } catch (SQLException e) {
            System.err.println("Error during debug: " + e.getMessage());
        }
    }
}