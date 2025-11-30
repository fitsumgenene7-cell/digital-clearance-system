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

        // Start with student login
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/student-login.fxml"));
        Scene scene = new Scene(loader.load(), 400, 400);

        stage.setTitle("Digital Clearance - Student Login");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}