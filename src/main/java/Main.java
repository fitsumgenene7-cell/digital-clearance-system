public class Main {
    public static void main(String[] args) {
        System.out.println("=== STARTING DATABASE SETUP ===");

        // Initialize and seed data
        DB.initializeDatabase();
        SeedData.seedStudents();
        SeedData.seedOffices();

        // Debug: Check what's actually in the database
        SeedData.debugDatabase();

        // Display data
        SeedData.displayAllStudents();
        SeedData.displayAllOffices();

        System.out.println("=== PROGRAM COMPLETED ===");
    }
}