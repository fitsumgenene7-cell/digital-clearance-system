package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Initialize database
        DB.initializeDatabase();
        SeedData.seedStudents();
        SeedData.seedOffices();

        // Start with hero page
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/hero-page.fxml"));
        Scene scene = new Scene(loader.load(), 800, 600);

        stage.setTitle("Digital Clearance System");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}