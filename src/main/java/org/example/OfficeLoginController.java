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

public class OfficeLoginController {

    @FXML private TextField officeNameField;
    @FXML private PasswordField passwordField;

    @FXML
    private void handleOfficeLogin() {
        String officeName = officeNameField.getText().trim();
        String password = passwordField.getText().trim();

        if (officeName.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please enter both office name and password");
            return;
        }

        try (Connection conn = DB.getConnection()) {
            String sql = "SELECT * FROM offices WHERE office_name = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, officeName);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Login successful - redirect to office dashboard
                redirectToOfficeDashboard(officeName);
            } else {
                showAlert("Error", "Invalid office name or password");
            }
        } catch (Exception e) {
            showAlert("Error", "Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void redirectToOfficeDashboard(String officeName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/office-dashboard.fxml"));
            Parent root = loader.load();

            // Pass office information to dashboard controller
            OfficeDashboardController controller = loader.getController();
            controller.initializeOffice(officeName);

            Stage stage = (Stage) officeNameField.getScene().getWindow();
            stage.setScene(new Scene(root, 600, 500));
            stage.setTitle(officeName + " Dashboard - Digital Clearance");

        } catch (Exception e) {
            showAlert("Error", "Error loading dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void switchToStudentLogin() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/student-login.fxml"));
            Stage stage = (Stage) officeNameField.getScene().getWindow();
            stage.setScene(new Scene(root, 400, 400));
            stage.setTitle("Student Login - Digital Clearance");
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