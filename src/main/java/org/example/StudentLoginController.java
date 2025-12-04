package org.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class StudentLoginController {

    @FXML private TextField studentIdField;
    @FXML private PasswordField passwordField;

    @FXML
    private void handleStudentLogin() {
        String studentId = studentIdField.getText().trim();
        String password = passwordField.getText().trim();

        if (studentId.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please enter both student ID and password");
            return;
        }

        try (Connection conn = DB.getConnection()) {
            String sql = "SELECT * FROM students WHERE id = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, studentId);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String studentName = rs.getString("first_name") + " " + rs.getString("last_name");
                
                // Redirect to student dashboard
                redirectToStudentDashboard(studentId, studentName);
                
            } else {
                showAlert("Error", "Invalid student ID or password");
            }
        } catch (Exception e) {
            showAlert("Error", "Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void redirectToStudentDashboard(String studentId, String studentName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/student-dashboard.fxml"));
            Parent root = loader.load();

            // Pass student information to dashboard controller
            StudentDashboardController controller = loader.getController();
            controller.initializeStudent(studentId, studentName);

            Stage stage = (Stage) studentIdField.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 700));
            stage.setTitle("Student Dashboard - Digital Clearance");

        } catch (Exception e) {
            showAlert("Error", "Error loading dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void switchToOfficeLogin() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/office-login.fxml"));
            Stage stage = (Stage) studentIdField.getScene().getWindow();
            stage.setScene(new Scene(root, 400, 400));
            stage.setTitle("Office Login - Digital Clearance");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}